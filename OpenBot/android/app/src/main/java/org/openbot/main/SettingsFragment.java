package org.openbot.main;

import static org.openbot.utils.Constants.PERMISSION_AUDIO;
import static org.openbot.utils.Constants.PERMISSION_BLUETOOTH;
import static org.openbot.utils.Constants.PERMISSION_CAMERA;
import static org.openbot.utils.Constants.PERMISSION_LOCATION;
import static org.openbot.utils.Constants.PERMISSION_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import org.openbot.R;
import org.openbot.utils.Constants;
import org.openbot.utils.PermissionUtils;
import org.openbot.vehicle.Vehicle;

import app.akexorcist.bluetotohspp.library.BluetoothState;
import timber.log.Timber;

public class SettingsFragment extends PreferenceFragmentCompat {
  private MainViewModel mViewModel;
  private SwitchPreferenceCompat connection;
  private SwitchPreferenceCompat bluetoothConnection;
  private Vehicle vehicle;
  private SwitchPreferenceCompat camera;
  private SwitchPreferenceCompat storage;
  private SwitchPreferenceCompat location;
  private SwitchPreferenceCompat mic;
  private SwitchPreferenceCompat bluetooth;
  private MainActivity mActivity;

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case Constants.BT_CONNECTED:
                    bluetoothConnection.setChecked(true);
                    mViewModel.setBluetoothStatus(vehicle.isBluetoothConnected());
                    break;

                case Constants.BT_CONNECTED_FAILED:
                case Constants.BT_DISCONNECTED:
                    bluetoothConnection.setChecked(false);
                    mViewModel.setBluetoothStatus(vehicle.isBluetoothConnected());
                    break;
            }
        };

    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mActivity.setHandler(mHandler);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mActivity.setHandler(mHandler);
    }

  private final ActivityResultLauncher<String[]> requestPermissionLauncher =
      registerForActivityResult(
          new ActivityResultContracts.RequestMultiplePermissions(),
          result ->
              result.forEach(
                  (permission, granted) -> {
                    switch (permission) {
                      case PERMISSION_CAMERA:
                        if (granted) camera.setChecked(true);
                        else {
                          camera.setChecked(false);
                          PermissionUtils.showCameraPermissionSettingsToast(requireActivity());
                        }
                        break;
                      case PERMISSION_STORAGE:
                        if (granted) storage.setChecked(true);
                        else {
                          storage.setChecked(false);
                          PermissionUtils.showStoragePermissionSettingsToast(requireActivity());
                        }
                        break;
                      case PERMISSION_LOCATION:
                        if (granted) location.setChecked(true);
                        else {
                          location.setChecked(false);
                          PermissionUtils.showLocationPermissionSettingsToast(requireActivity());
                        }
                        break;
                      case PERMISSION_AUDIO:
                        if (granted) mic.setChecked(true);
                        else {
                          mic.setChecked(false);
                          PermissionUtils.showAudioPermissionSettingsToast(requireActivity());
                        }
                        break;
                        case PERMISSION_BLUETOOTH:
                            if (granted) bluetooth.setChecked(true);
                            else {
                                bluetooth.setChecked(false);
                                PermissionUtils.showBluetoothPermissionLoggingToast(requireActivity());
                            }
                            break;
                    }
                  }));

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.root_preferences, rootKey);

    mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    vehicle = mViewModel.getVehicle().getValue();

    connection = findPreference("connection");
    if (connection != null) {
      connection.setTitle("No Device");
      if (vehicle != null && vehicle.isUsbConnected()) {
        connection.setChecked(true);
        connection.setTitle(vehicle.getUsbConnection().getProductName());
      } else {
        connection.setTitle("No Device");
        connection.setChecked(false);
      }
      connection.setOnPreferenceClickListener(
          preference -> {
            Timber.d(String.valueOf(connection.isChecked()));
            if (vehicle != null) {
              if (connection.isChecked()) {
                vehicle.connectUsb();
                if (vehicle.isUsbConnected())
                  connection.setTitle(vehicle.getUsbConnection().getProductName());
                else {
                  connection.setTitle("No Device");
                  connection.setChecked(false);
                  Toast.makeText(
                          requireContext().getApplicationContext(),
                          "Please check the USB connection.",
                          Toast.LENGTH_SHORT)
                      .show();
                }
              } else {
                vehicle.disconnectUsb();
                connection.setTitle("No Device");
              }
              mViewModel.setUsbStatus(vehicle.isUsbConnected());
            }
            return true;
          });
    }

      bluetoothConnection = findPreference("bluetooth_connection");
      if (bluetoothConnection != null) {
          bluetoothConnection.setTitle("No Device");
          if (vehicle != null && vehicle.isBluetoothConnected()) {
              bluetoothConnection.setChecked(true);
              bluetoothConnection.setTitle(vehicle.getBluetoothConnection().getProductName());
          } else {
              bluetoothConnection.setTitle("No Device");
              bluetoothConnection.setChecked(false);
          }
          bluetoothConnection.setOnPreferenceClickListener(
                  preference -> {
                      Timber.d(String.valueOf(bluetoothConnection.isChecked()));
                      if (vehicle != null) {
                          if (bluetoothConnection.isChecked()) {
                              vehicle.connectBluetooth(getActivity());

                          } else {
                              vehicle.disconnectBluetooth();
                              bluetoothConnection.setTitle("No Device");
                          }
                          mViewModel.setBluetoothStatus(vehicle.isBluetoothConnected());
                      }
                      return true;
                  });
      }

    camera = findPreference("camera");
    if (camera != null) {
      camera.setChecked(PermissionUtils.hasCameraPermission(requireActivity()));
      camera.setOnPreferenceChangeListener(
          (preference, newValue) -> {
            if (camera.isChecked())
              PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
            else {
              if (!PermissionUtils.shouldShowRational(
                  requireActivity(), Constants.PERMISSION_CAMERA)) {
                PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
              } else {
                requestPermissionLauncher.launch(new String[] {Constants.PERMISSION_CAMERA});
              }
            }

            return false;
          });
    }

    storage = findPreference("storage");
    if (storage != null) {
      storage.setChecked(PermissionUtils.hasStoragePermission(requireActivity()));
      storage.setOnPreferenceChangeListener(
          (preference, newValue) -> {
            if (storage.isChecked())
              PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
            else {
              if (!PermissionUtils.shouldShowRational(
                  requireActivity(), Constants.PERMISSION_STORAGE)) {
                PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
              } else requestPermissionLauncher.launch(new String[] {Constants.PERMISSION_STORAGE});
            }

            return false;
          });
    }

    location = findPreference("location");
    if (location != null) {
      location.setChecked(PermissionUtils.hasLocationPermission(requireActivity()));
      location.setOnPreferenceChangeListener(
          (preference, newValue) -> {
            if (location.isChecked())
              PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
            else {
              if (!PermissionUtils.shouldShowRational(requireActivity(), PERMISSION_LOCATION)) {

                PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
              } else requestPermissionLauncher.launch(new String[] {PERMISSION_LOCATION});
            }

            return false;
          });
    }

    mic = findPreference("mic");
    if (mic != null) {
      mic.setChecked(PermissionUtils.hasAudioPermission(requireActivity()));
      mic.setOnPreferenceChangeListener(
          (preference, newValue) -> {
            if (mic.isChecked())
              PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
            else {
              if (!PermissionUtils.shouldShowRational(
                  requireActivity(), Constants.PERMISSION_AUDIO)) {
                PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
              } else requestPermissionLauncher.launch(new String[] {Constants.PERMISSION_AUDIO});
            }
            return false;
          });
    }

      bluetooth = findPreference("bluetooth");
      if (bluetooth != null) {
          bluetooth.setChecked(PermissionUtils.hasBluetoothPermissions(requireActivity()));
          bluetooth.setOnPreferenceChangeListener(
                  (preference, newValue) -> {
                      if (bluetooth.isChecked())
                          PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
                      else {
                          if (!PermissionUtils.shouldShowRational(
                                  requireActivity(), PERMISSION_BLUETOOTH)) {
                              PermissionUtils.startInstalledAppDetailsActivity(requireActivity());
                          } else requestPermissionLauncher.launch(new String[] {PERMISSION_BLUETOOTH});
                      }
                      return false;
                  });
      }

    ListPreference streamMode = findPreference("video_server");

    if (streamMode != null)
      streamMode.setOnPreferenceChangeListener(
          (preference, newValue) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle(R.string.confirm_title);
            builder.setMessage(R.string.stream_change_body);
            builder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                  streamMode.setValue(newValue.toString());
                  restartApp();
                });
            builder.setNegativeButton(
                "Cancel", (dialog, id) -> streamMode.setValue(streamMode.getEntry().toString()));
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
          });
  }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        vehicle.getBluetoothConnection().onDeviceListReturn(requestCode, resultCode, intent);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                mViewModel.setBluetoothStatus(true);
        }else
            mViewModel.setBluetoothStatus(false);
    }

  private void restartApp() {
    new Handler()
        .postDelayed(
            () -> {
              final PackageManager pm = requireActivity().getPackageManager();
              final Intent intent =
                  pm.getLaunchIntentForPackage(requireActivity().getPackageName());
              requireActivity().finishAffinity(); // Finishes all activities.
              requireActivity().startActivity(intent); // Start the launch activity
              System.exit(0); // System finishes and automatically relaunches us.
            },
            100);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mViewModel
        .getUsbStatus()
        .observe(
            getViewLifecycleOwner(),
            status -> {
              if (connection != null) {
                connection.setChecked(status);
                connection.setTitle(
                    status ? vehicle.getUsbConnection().getProductName() : "No Device");
              }
            });

      mViewModel
              .getBluetoothStatus()
              .observe(
                      getViewLifecycleOwner(),
                      status -> {
                          if (bluetoothConnection != null) {
                              bluetoothConnection.setChecked(status);
                              if(vehicle.isBluetoothConnected())
                              bluetoothConnection.setTitle(
                                      status ? vehicle.getBluetoothConnection().getProductName() : "No Device");
                              else bluetoothConnection.setTitle( status ? "connecting, please wait" : "No Device");
                          }
                      });

  }
}

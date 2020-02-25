package kimsunho.medicalsignalendcoderapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

                                    /*   블루투스 통신 클래스   */
                        /*   안드로이드 기기에서 페어링 설정 해줘야 함    */
public class BluetoothService{

    BluetoothAdapter bluetoothAdapter;                      // 블루투스 통신 어댑터
    InputStream mInputStream;                               // Read 스트림 변수
    Thread mWorkerThread;                                   // 쓰레드 변수
    BluetoothSocket bluetoothSocket;                        // 블루투스의 소켓을 생성하기 위한 소켓 변수

    boolean NETWORK;                                           // 블루투스 연결
    boolean START;                                             // 데이터 연결 시작
    int dataindex;                                             // 데이터 Sample Size만큼
    int bdata;                                                  // 데이터 읽어오기
    long startTime=0;                                          // Sampling rate 시작
    long endTime=0;                                            // Sampling rate 끝
    String deviceName="";                                      // 디바이스 이름

    ArrayList<String> listItem;                                 // 디바이스 이름 저장하는 배열리스트
    ArrayList<Integer> datalist;
    Set<BluetoothDevice> pairedDeivces;                         // 페어링 된 디바이스
    Context context;                                             // 해당 클래스의 정보를 저장하기 위한 것

                                    /*   블루투스 통신 클래스 초기화   */
    public BluetoothService(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    // 블루투스 디폴트 어댑터 설정
        this.context=context;                                   // 해당 액티비티의 context 저장 -> Form 액티비티

        NETWORK=false;                                          // 블루투스 통신 연결 확인
        START=false;                                            // 블루투스 통신 시작
        dataindex=0;                                            // 데이터가 얼마나 들어왔는지 확인 하기 위한 것

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) { // 블루투스 통신 확인
        }
        else
        {
            NETWORK=true;                               // 연결 할 수 있는 조건을 만족하면 네트워크 연결 설정
        }
    }
                                        /*   디바이스 선택 메소드   */
    public void selectDevice() {
        pairedDeivces = bluetoothAdapter.getBondedDevices();                    // 페어링 할 수 있는 디바이스 확인
        listItem = new ArrayList<String>();                                         // 초기화
        datalist=new ArrayList<>();
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);     // 해당 액티비티에 다이얼로그 창 띄우기 위한 것
        builder.setTitle("Device Select");              //  디바이스 선택 할 수 있는 다이얼로그 타이틀 설정

        if (pairedDeivces.size() == 0) {
            Toast.makeText(context, "Paired Device is not Searching", Toast.LENGTH_SHORT).show();   // 페어링 된 디바이스가 존재 하지 않는다면 토스트 메시지 출력
        }

        else
        {
            for(BluetoothDevice device : pairedDeivces)                 // 페어링 된 디바이스를 순차적으로 저장
                listItem.add(device.getName().toString());              // 스트링 형식으로 저장

            listItem.add("Cancel");                 // 블루투스 통신 하지 않을 경우
        }

        final CharSequence[] items = listItem.toArray(new CharSequence[listItem.size()]);       // char형 배열로 변환 -> 알림창 에서 필요로 하기 때문

        builder.setItems(items, new DialogInterface.OnClickListener() {                     // 알림창에서 해당 디바이스를 선택하였을 경우 이벤트 설정
            @Override
            public void onClick(DialogInterface dialog, int which) {                //  클릭 이벤트
                if(listItem.size()-1==which)
                {
                    ((FormActivity)FormActivity.context).StoreFunction(false);
                }
                else
                {
                    ((FormActivity)FormActivity.context).StoreFunction(true);
                    deviceName=listItem.get(which);                 // 해당 디바이스 이름을 눌렀을 경우
                    connectToSelectdDevice(deviceName);               // 디바이스 이름 전달 -> 블루투스 통신 하기 위한 것
                }
            }
        });

        AlertDialog alert = builder.create();               // 알림창 생성 -> 눌렸을 경우 -> 블루투스 연결 버튼
        alert.show();                                       // 알림창 보여줌
    }
                                            /*   블루투스 통신 연결 하기위한 메소드   */
    void connectToSelectdDevice(String selectedDeviceName) {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");            // 블루투스 uuid

        BluetoothDevice selectedDevice = null;                      // 디바이스 선택

        for(BluetoothDevice device : pairedDeivces) {               // 페어링 된 디바이스 과 선택 디바이스와 맞는지 확인
            if(selectedDeviceName.equals(device.getName())) {       // 해당 이름과 같은 경우
                selectedDevice = device;            // 디바이스 선택
                break;
            }
        }

        try {
            bluetoothSocket=selectedDevice.createInsecureRfcommSocketToServiceRecord(uuid);         // 해당 디바이스의 소켓 생성
            bluetoothSocket.connect();          // 디바이스 연결

            mInputStream=bluetoothSocket.getInputStream();          // 스트림 생성

            beginListenForData();               //데이터 수신하기위한 메소드
        }
        catch(Exception e) {
            Log.e("Bluetooth Error",e.getMessage());                // 블루투스 에러발생시 생성 메시지
            Toast.makeText(context, "Bluetooth Error", Toast.LENGTH_SHORT).show();                 // 에러 토스트 메시지
            NETWORK=false;          // 연결 종료
        }
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();                      // 핸들러 생성
        ((WaveActivity)WaveActivity.context).wavedataList.clear();                      // 파형 데이터 초기화 시킴 -> 들어온 데이터를 그려줘야 하기 때문에

        mWorkerThread = new Thread(new Runnable() {                                             // 문자열 수신 쓰레드

            public void run() {                     // 쓰레드 실행 중인 메소드

                while (!Thread.currentThread().isInterrupted()) {           // 인터럽트 발생이 없다면

                    try {
                        if(mInputStream!=null)                  // 스트림이 있을 경우
                        {
                             int bytesAvailable = mInputStream.available();                                // 수신 데이터 확인
                             if (bytesAvailable > 0) {                                                             // 데이터가 수신된 경우
                                 bdata = mInputStream.read();                               // 데이터를 읽어옴
                                 datalist.add(bdata);
                                 dataindex++;                   // 들어온 데이터의 index를 실시간으로 저장
                                 endTime=startTime;           // 수신된 데이터의 경과 시간
                                 startTime=System.currentTimeMillis();      // 들어온 시간

                                 handler.post(new Runnable() {                                     //수신된 데이터 처리
                                     public void run() {
                                         ((WaveActivity) WaveActivity.context).realTimeWaveviewer(bdata);           // 실시간으로 그래프를 그리기 위한것
                                         ((FormActivity)FormActivity.context).DataCheckFunction(dataindex,dataindex/50,startTime-endTime,bdata);      // Fomr 액티비티 메소트 -> 데이터를 체크 하기위한 메소드
                                     }
                               });
                            }
                        }
                    } catch (IOException ex) {
                        Log.e("IO Error", ex.getMessage());         // 에러메시지 출력
                        // 데이터 수신 중 오류 발생.
                    }
                }
            }
        });

        mWorkerThread.start();          // 쓰
    }
}

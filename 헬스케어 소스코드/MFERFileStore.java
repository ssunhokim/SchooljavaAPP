package kimsunho.medicalsignalendcoderapplication;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by 김선호 on 2016-07-14.
 */
public class MFERFileStore {

    ArrayList<Integer> headerData;      // 헤더 저장

    int channelCount;               // 채널 수
    int blockCount;                 // 블락 크기
    int seqCount;                   // 반복수
    int ENDIAN;                     // 엔디안 크기
    int TYPE;                       // 데이터 타입

                /*   초기화   */
    public MFERFileStore() {
        headerData=new ArrayList<Integer>();
        channelCount=0;
        ENDIAN=0;
        TYPE=0;
        int temp;
    }

    void WaveStore(String filename,int result)
    {
        FileOutputStream ioFs;

        int data1=0;
        int data2=0;
        int data3=0;
        int data4=0;

        try {
            ioFs=new FileOutputStream(filename,true);

            if (TYPE == 0) {
                data1 = result / 256;
                data2 = result % 256;

                ioFs.write(data1);
                ioFs.write(data2);
            } else if (TYPE == 1) {
                data1 = result / 256;
                data2 = result % 256;

                ioFs.write(data1);
                ioFs.write(data2);
            } else if (TYPE == 2) {
                    /*   32비트 크기 저장  */
            } else if (TYPE == 3) {
                data1 = result;
                ioFs.write(data1);
            } else if (TYPE == 4) {
                    /*   16비트 Status???  */
            } else if (TYPE == 5) {
                data1 = result;
                ioFs.write(data1);
            } else if (TYPE == 6) {
                    /*   32비트 크기 저장  */
            } else if (TYPE == 7) {

            } else if (TYPE == 8) {

            } else if (TYPE == 9) {

            } else {

            }

        }catch (Exception e)
        {
            Log.e("File","File Error"+e.getMessage());
        }
    }
                            /*   헤더를 저장함   */
    void HeaderStore(String filename)
    {
        FileOutputStream ioFs;

        try {
            ioFs=new FileOutputStream(filename);

            for(int i=0;i<headerData.size();i++)
                ioFs.write(headerData.get(i));

        }catch (Exception e)
        {
            Log.e("File","File Error"+e.getMessage());
        }
    }

    /*   데이터의 해당 값들을  저장 하는 메소드   */
    void DataStore(String tag,String data)                    // 태그 내용 ,값의 내용
    {
        int value;                                  // 태그 값
        String str;                                 // 표본추출 데이터 저장하기 위한 변수 -> 계산용 변수

        value=TagData(tag);                         // 헤더의 내용과 같을 때 태그의 값을 전달
        headerData.add(value);

        switch (value)
        {
            case 64:                                    // 서문 저장
                headerData.add(data.length());         // 데이터의 길이

                for(int i=0;i<data.length();i++)        // 데이터의 길이만큼
                    headerData.add((int)data.charAt(i));        // 데이터 저장

                break;
            case 5:             // 채널 수 저장
                channelCount=Integer.valueOf(data);             // 채널의 수를 저장
                EndianConstructionStore(value,Integer.valueOf(data));       // 엔디안 구조에 따라 저장
                break;
            case 11:            // 표본 추출 비율 저장
                headerData.add(4);      // 길이저장

                                /*   단위 별로 저장   */
                if(data.contains("Hz"))                 // Hz면 0저장
                    headerData.add(0);
                else if(data.contains("Sec"))           // Sec면 1저장
                    headerData.add(1);
                else                            // m면 2저장
                    headerData.add(2);

                /*   Exponent 저장   */
                if(data.contains("0")) {            // 첫번쨰 0이 있을경우 그길이 만큼 뺌
                    headerData.add(data.length() - data.indexOf("0"));
                    str=data.replace("0","");       // 0 대체
                }
                else {              // 소수 점일 경우
                    headerData.add(data.length() - data.indexOf("."));
                    str=data.replace(".","");       // 소수점 대체
                }
                            /*   Mantissa 저장   */
                EndianConstructionStore(value,Integer.valueOf(str));

                break;
            case 12:                    // 표본추출 해상도
                headerData.add(4);      // 길이 저장
                headerData.add(0);      // 단위가 Volt 밖에 없다

                                        /*   Exponent 저장   */
                if(data.contains("0")) {
                    if(data.contains("uV"))
                        headerData.add(data.length() - data.indexOf("0")+7);
                    else
                        headerData.add(data.length() - data.indexOf("0"));

                    str=data.replace("0","");
                }
                else {
                    if(data.contains("uV"))
                        headerData.add(data.length() - data.indexOf(".")+7);
                    else
                        headerData.add(data.length() - data.indexOf("."));

                    str=data.replace(".","");
                }
                            /*   Mantissa 저장   */
                EndianConstructionStore(value,Integer.valueOf(str));

                break;
            case 4:             // 블록크기
                blockCount=Integer.valueOf(data);
                EndianConstructionStore(value,Integer.valueOf(data));
                break;
            case 6:             // 반복 수
                seqCount=Integer.valueOf(data);
                EndianConstructionStore(value,Integer.valueOf(data));
                break;
            case 8:                 // 파형 타입
                if(WaveType(data)==10000)
                {
                    headerData.add(data.length());

                    for (int i = 0; i < data.length(); i++)
                        headerData.add((int) data.charAt(i));

                }
                else
                    EndianConstructionStore(value,WaveType(data));

                break;
            case 9:             // 체널별 파형 속성
                if(WaveAtt(data)==10000) {
                    headerData.add(data.length());

                    for (int i = 0; i < data.length(); i++)
                        headerData.add((int) data.charAt(i));
                }
                else
                    EndianConstructionStore(value,WaveAtt(data));
                break;
            case 30:                                               // 파형길이 -> 구할필요가??
                headerData.add(131);
                headerData.add(0);
                headerData.add(0);
                headerData.add(0);
                break;
            case 63:                                               //
                headerData.add(channelCount);
                headerData.add(4);
                break;
            case 10:                        // 파형데이터 타입
                headerData.add(1);
                TYPE=DataType(data);
                headerData.add(TYPE);
                break;
            case 13:                // 오프셋
                EndianConstructionStore(value,Integer.valueOf(data));
                break;
            case 18:                // 널 값
                EndianConstructionStore(value,Integer.valueOf(data));
                break;
            case 14:
                /*   압축 코드   */
                break;
            case 1:             // 엔디안
                headerData.add(1);

                if(data.equals("Big-Endian"))
                    ENDIAN=0;
                else
                    ENDIAN=1;

                headerData.add(ENDIAN);

                break;
            case 7:
                /*   지시자 코드   */
                break;
            case 23:                // 제조사 이름
                headerData.add(data.length());

                for(int i=0;i<data.length();i++)
                    headerData.add((int)data.charAt(i));

                break;
            case 65:
                /*   이벤트 코드   */
            case 21:
                /*   파형 정보 코드   */
            case 22:                    // 주석 저장
                headerData.add(data.length());

                for(int i=0;i<data.length();i++)
                    headerData.add((int)data.charAt(i));

                break;
            case 2:
                /*   버전 코드   */
            case 3:
                /*   문자 코드   */
            case 17:
                headerData.add(data.length());

                for(int i=0;i<data.length();i++)
                    headerData.add((int)data.charAt(i));

                break;
            case 15:
                /*   보간법 코드   */
            case 66:
                /*   측정치 코드   */
            case 67:
                /*   표본추출 비대칭 코드   */
            case 103:
                /*   그룹 정의 코드   */
            case 136:
                /*   기술 맵 코드   */
            case 69:
                /*   참조지시자 코드   */
            case 70:
                /*   디지털 사인 코드   */
            case 129:
                headerData.add(data.length());

                for(int i=0;i<data.length();i++)
                    headerData.add((int)data.charAt(i));

                break;
            case 130:
                headerData.add((data.length()));

                for(int i=0;i<data.length();i++)
                    headerData.add((int)data.charAt(i));

                break;
            case 131:
                /*   환자 나이   */
                break;
            case 132:
                headerData.add(1);

                if(data.equals("Unclear"))
                    headerData.add(0);
                else if(data.equals("Male"))
                    headerData.add(1);
                else if(data.equals("Female"))
                    headerData.add(2);
                else
                    headerData.add(3);

                break;
            case 133:
                /*   측정 일시   */
                break;
            case 134:
                break;
            default:
                break;
        }
    }
                    /*   엔디안 구조에 따라 저장하는 함수   */
    private void EndianConstructionStore(int tag,int temp)
    {
        if(ENDIAN==0)
        {
            if(temp<256)
            {
                if(tag==8 || tag==9)
                {
                    headerData.add(2);
                    headerData.add(0);
                    headerData.add(temp);
                }

                else if(tag==11 || tag==12)
                {
                    headerData.add(0);
                    headerData.add(temp);
                }
                else
                {
                    headerData.add(1);
                    headerData.add(temp);
                }
            }
            else if(temp>=256 && temp<65536)
            {
                if(tag==11 || tag==12)
                {
                    headerData.add(temp/256);
                    headerData.add(temp%256);
                }

                headerData.add(2);
                headerData.add(temp/256);
                headerData.add(temp%256);
            }
        }
        else
        {
            if(temp<256)
            {
                if(tag==8 || tag==9) {
                    headerData.add(2);
                    headerData.add(temp);
                    headerData.add(0);
                }
                else if(tag==11 || tag==12)
                {
                    headerData.add(temp);
                    headerData.add(0);
                }
                else
                {
                    headerData.add(1);
                    headerData.add(temp);
                }
            }
            else if(temp>=256 && temp<65536)
            {
                if(tag==11 || tag==12)
                {
                    headerData.add(temp%256);
                    headerData.add(temp/256);
                }

                headerData.add(2);
                headerData.add(temp%256);
                headerData.add(temp/256);
            }
        }
    }

    /*   해당 String 값에 다라 태그값 전달   */
    private int TagData(String tag) {
        if (tag.equals("Preface"))
            return 64;
        else if (tag.equals("Channels"))
            return 5;
        else if (tag.equals("Sampling rate"))
            return 11;
        else if (tag.equals("Resolution"))
            return 12;
        else if (tag.equals("Data Block"))
            return 4;
        else if (tag.equals("Sequence"))
            return 6;
        else if (tag.equals("WaveType"))
            return 8;
        else if (tag.equals("Properties"))
            return 9;
        else if (tag.equals("Wave Data"))
            return 30;
        else if (tag.equals("Channel Number"))
            return 63;
        else if (tag.equals("WaveData Type"))
            return 10;
        else if (tag.equals("Offset"))
            return 13;
        else if (tag.equals("Null"))
            return 18;
        else if (tag.equals("Compression"))
            return 14;
        else if (tag.equals("Endian"))
            return 1;
        else if (tag.equals("Pointer"))
            return 7;
        else if (tag.equals("Manufacturer"))
            return 23;
        else if (tag.equals("Event"))
            return 65;
        else if (tag.equals("Wave Info"))
            return 21;
        else if (tag.equals("Remark"))
            return 22;
        else if (tag.equals("Version"))
            return 2;
        else if (tag.equals("Character Code"))
            return 3;
        else if (tag.equals("Filter"))
            return 17;
        else if (tag.equals("Interpolation"))
            return 15;
        else if (tag.equals("Measure"))
            return 66;
        else if (tag.equals("Sampling Asymmetric"))
            return 67;
        else if (tag.equals("Group"))
            return 103;
        else if (tag.equals("Knowledge Map"))
            return 136;
        else if (tag.equals("Indicators"))
            return 69;
        else if (tag.equals("Digital Sign"))
            return 70;
        else if (tag.equals("Patient Name"))
            return 129;
        else if (tag.equals("Patient ID"))
            return 130;
        else if (tag.equals("Age"))
            return 131;
        else if (tag.equals("Patient Sex"))
            return 132;
        else if (tag.equals("Measurement"))
            return 133;
        else if (tag.equals("Message"))
            return 134;
        else
            return 10000;
    }
        /*   파형속성 String 값에따른 파형 값 저장   */
    private int WaveType(String value)
    {
        if(value.equals("Unidentified"))
            return 0;
        else if(value.equals("Standard 12 lead ECG"))
            return 1;
        else if(value.equals("Long-term ECG"))
            return 2;
        else if(value.equals("Vectorcardiogram"))
            return 3;
        else if(value.equals("Stress ECG"))
            return 4;
        else if(value.equals("Intracardiac ECG"))
            return 5;
        else if(value.equals("Body surface ECG"))
            return 6;
        else if(value.equals("Ventricular late potential"))
            return 7;
        else if(value.equals("Body surface late potential"))
            return 8;
        else if(value.equals("PCG etc"))
            return 30;
        else if(value.equals("Fingertip pulse, carotid pulse"))
            return 31;
        else if(value.equals("Long-term waveform"))
            return 20;
        else if(value.equals("Sampled waveform"))
            return 21;
        else if(value.equals("Power spectrum"))
            return 25;
        else if(value.equals("Trendgram"))
            return 26;
        else if(value.equals("MCG"))
            return 100;
        else if(value.equals("Resting EEG"))
            return 40;
        else if(value.equals("Evoked EEG"))
            return 41;
        else if(value.equals("Frequency analysis"))
            return 42;
        else if(value.equals("Long-term EEG"))
            return 43;
        else
            return 10000;
    }
    /*   파형속성 String 값에따른 파형 값 저장   */
    private int WaveAtt(String value)
    {
        if(value.equals("I"))
            return 1;
        else if(value.equals("II"))
            return 2;
        else if(value.equals("V1"))
            return 3;
        else if(value.equals("V2"))
            return 4;
        else if(value.equals("V3"))
            return 5;
        else if(value.equals("V4"))
            return 6;
        else if(value.equals("V5"))
            return 7;
        else if(value.equals("V6"))
            return 8;
        else if(value.equals("V7"))
            return 9;
        else if(value.equals("V3R"))
            return 11;
        else if(value.equals("V4R"))
            return 12;
        else if(value.equals("V5R"))
            return 13;
        else if(value.equals("V6R"))
            return 14;
        else if(value.equals("V7R"))
            return 15;
        else if(value.equals("III"))
            return 61;
        else if(value.equals("aVR"))
            return 62;
        else if(value.equals("aVL"))
            return 63;
        else if(value.equals("aVF"))
            return 64;
        else if(value.equals("V8"))
            return 66;
        else if(value.equals("V9"))
            return 67;
        else if(value.equals("V8R"))
            return 68;
        else if(value.equals("V9R"))
            return 69;
        else
            return 10000;
    }
        /*   데이터 타입에 따른 속성값 저장   */
    private int DataType(String value) {
        if (value.equals("Signed 16bits integer"))
            return 0;
        else if (value.equals("Unsigned 16bits integer"))
            return 1;
        else if (value.equals("Signed 32bits integer"))
            return 2;
        else if (value.equals("Unsigned 8bits integer"))
            return 3;
        else if (value.equals("16bits status"))
            return 4;
        else if (value.equals("Signed 8bits integer"))
            return 5;
        else if (value.equals("Unsigned 32bits integer"))
            return 6;
        else if (value.equals("32bits singled-precision floating"))
            return 7;
        else if (value.equals("64bits double-precision floating"))
            return 8;
        else if (value.equals("8bits AHA differential"))
            return 9;
        else
            return 10;
    }
}

package kimsunho.medicalsignalendcoderapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
        - 파일 읽을때 정의하지 못한 것도 있어서 버그가 있을 수 있다
 */

public class MFERDataProcedure {

    final int BYTESIZE=256;                         // 1바이트 사이즈 지정
    Context context;                                // 호출한 액티비티 Context 저장하기 위한 것
    int ENDIAN;                                      //엔디안 설정
    int datatype;                                   //데이터 속성
    int datasize;                                   //데이터 속성 -> 데이터 크기를 저장하기 위한 것
    int datablock;                                  //데이터 블록 크기
    int channelcount;                              //데이터 채널 수
    int sequence;                                   //시퀀스 수
    int waveindex;                                  //파형 위치 지정

    ArrayList<Integer> tagdata;                           //태그 데이터 값 저장
    ArrayList<Integer> lengthdata;                       //길이 데이터 값 저장
    ArrayList<Integer> valuedata;                        //실질적인 데이터 값 저장
    ArrayList<String> tagstring;                         //태그 값 저장
    ArrayList<String> valuestring;                       //실질적인 값 저장
    ArrayList<String> waveatt;                            //채널별 파형 속성 저장

    public MFERDataProcedure(String fileName)
    {
        File file = new File(fileName);                     //파일 생성
        FileInputStream inFs = null;                        //스트림 생성 변수
        String str="";                                      //데이터값을 저장하는 변수

        ENDIAN=0;                                                            //엔디안 디폴트 설정
        datatype=0;                                                          //데이터 유형 디폴트
        channelcount=0;                                                     //채널 수 디폴트
        datablock=0;                                                        //데이터 블록 수 디폴트
        sequence=0;                                                         //시퀀스 디폴트
        waveindex=0;                                                        //파형 위치 디폴트

                                            /*   리스트 변수 초기화   */
        tagdata=new ArrayList<>();
        lengthdata=new ArrayList<>();
        valuedata=new ArrayList<>();
        tagstring=new ArrayList<>();
        valuestring=new ArrayList<>();
        waveatt=new ArrayList<>();

        int tag;                // 태그값 저장
        int length;             // 길이 값 저장
        int[] value=new int[100];       // value 값을 저장

        if (!file.exists()) {           // 파일이 존재 하지 않을 경우
            Toast toast = Toast.makeText(context.getApplicationContext(), "파일이 존재하지 않습니다", Toast.LENGTH_SHORT);
            toast.show();
        }

        else {

            try {
                inFs = new FileInputStream(fileName);           // 해당 파일을 불러옴

                while (true)
                {
                    tag=inFs.read();                    // 태그값 저장
                    tagdata.add(tag);                 // 태그 값 배열리스트에 저장
                    str="";                             // 값에 해당하는 String 값을 전달하기 위한 변수

                    waveindex++;                        // 파형 인덱스

                    if (tag == 63) {                // 채널 넘버 태그
                        value[0] = inFs.read();         // 채널 넘버 저장
                        length = inFs.read();           // 길이 저장
                        lengthdata.add(length);         // 길이 배열리스트에 저장

                        waveindex++;                // 파형 인덱스 증가
                        waveindex++;

                        tagstring.add(TagString(tag));          // Tag값의 String 형태로 저장
                        valuestring.add(MFERProcedure(tag,length,value));   //Value 값의 String 형태로 저장
                    }

                    else if(tag==30)            // 태그가 30 -> 파형 자료
                    {
                        length=inFs.read();         // 길이 저장
                        lengthdata.add(length);         // 길이 배열리스트에 저장

                        waveindex++;                // 파형 인덱스 증가

                        if(length>=128)             // 길이가 128 이상일 경우
                        {
                            for(int i=0;i<length-128;i++) {
                                inFs.read();                // 128을 뺀만큼 건너뜀 -> 어차피 파형길이는 구할 수 있다.
                                waveindex++;
                            }
                        }

                        else
                            waveindex++;

                        break;
                    }

                    else {              // 그외 태그값 처리
                        length = inFs.read();       // 길이 저장
                        lengthdata.add(length);     // 길이 배열리스트에 저장

                        waveindex++;

                        for (int i = 0; i < length; i++) {
                            value[i] = inFs.read();             // Value 값 읽어옴 -> 길이만큼
                            valuedata.add(value[i]);        // Value값 저장
                            str+=String.valueOf(value[i])+"  ";         // String 형태
                            waveindex++;            // 파형인덱스 증가
                        }

                        tagstring.add(TagString(tag));                  // 태그값 String 형태로 저장
                        valuestring.add(MFERProcedure(tag,length,value));           // Value 값 String 형태로 저장 -> 실질적인 값임
                    }

                }

            } catch (Exception e) {
                Log.e("File", "File read Error" + e);
            }
        }
    }

    String MFERProcedure(int tag,int length,int[] value)                             //MFER 데이터 계산 -> 저장 메서드
    {
        /*   계산하기 위한 변수   */
        String str="";
        int num=0;
        double num2=0;
        int num1=0;
        char[] str1=new char[100];

        /*   나이 or 측정일시 변수   */
        int years;
        int days;
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;
        int millsecond;
        int microsecond;

        years=days=year=month=day=hour=minute=second=millsecond=microsecond=0;

        switch(tag)
        {
            case 11:                // 표본 추출 비율
                num = Caculation(length, value, ENDIAN, 2);             // 데이터 계산
                num1=value[1];

                /*   음수 일 경우   */
                if(value[1]>=128) {
                    num1=value[1];
                    num1=256-num1;
                    num1++;

                    int ten=1;

                    /*   Exponent계산   */
                    for(int i=0;i<num1;i++)
                        ten*=10;
                    /*   지수가 음수 일경우 소수로 나타내야 하므로   */
                    num2=(double)num/ten;

                    /*   단위를 알기 위한 것   */
                    if(value[0]==0)
                        str = String.valueOf(num2)+"HZ";
                    else if(value[0]==1)
                        str = String.valueOf(num2)+"Sec";
                    else
                        str = String.valueOf(num2)+"m";
                }
                /*   양수 일경우   */
                else
                {
                    for(int i=0;i<num1;i++)
                        num*=10;

                    /*   단위를 알기 위한 것   */
                    if(value[0]==0)
                        str = String.valueOf(num)+"Hz";
                    else if(value[0]==1)
                        str = String.valueOf(num)+"Sec";
                    else
                        str = String.valueOf(num)+"m";
                }

                break;
            case 12:            // 표본 추출 해상도 -> 표본 추출 비율과 같다
                num = Caculation(length, value, ENDIAN, 2);

                int ten=1;
                /*   음수일 경우   */
                if(value[1]>=128) {
                    num1=value[1];
                    num1=256-num1;

                    if(num1>=7)
                    {
                        for (int i = 0; i < num1-7; i++)
                            ten*=10;

                        num2=(double)num/ten;

                        str = String.valueOf(num2) + "uV";
                    }

                    else {
                        for (int i = 0; i < num1; i++)
                            ten*=10;

                        num2=(double)num/ten;

                        str = String.valueOf(num2) + "V";
                    }
                }

                else
                {
                    for(int i=0;i<num1;i++)
                        num*=10;

                    str = String.valueOf(num)+"V";
                }

                break;
            case 4:             // 데이터 블록
                num=Caculation(length, value, ENDIAN);              // 데이터 계산 메서드
                datablock=num;                              // 블락 크기 저장
                str=String.valueOf(num);
                break;
            case 5:             // 채널 수
                num=Caculation(length, value, ENDIAN);
                channelcount=num;           // 채널 수 저장
                str=String.valueOf(num);
                break;
            case 6:
                num=Caculation(length, value, ENDIAN);
                sequence=num;           // 반복 수 저장
                str=String.valueOf(num);
                break;
            case 8:
                if(length==2)
                {
                    num=Caculation(length,value,ENDIAN);
                    str=WaveType(num);
                }
                else
                {
                    for(int i=2;i<length;i++)
                        str1[i]=(char)value[i];

                    str=String.valueOf(str1);
                }
                break;
            case 9:
                if(length==2)
                {
                    num=Caculation(length,value,ENDIAN);
                    str=WaveAtt(num);
                }
                else
                {
                    for(int i=2;i<length;i++)
                        str1[i]=(char)value[i];

                    str=String.valueOf(str1);
                }

                waveatt.add(str);
                break;
            case 30:
                break;
            case 63:
                str=String.valueOf(value[0]);
                break;
            case 10:
                str=DataType(value[0]);
                datatype=value[0];

                switch (datatype)
                {
                    case 0:
                        datasize=2;
                        break;
                    case 1:
                        datasize=2;
                        break;
                    case 2:
                        datasize=4;
                        break;
                    case 3:
                        datasize=1;
                        break;
                    case 4:
                        datasize=2;
                        break;
                    case 5:
                        datasize=1;
                        break;
                    case 6:
                        datasize=4;
                        break;
                    case 7:
                        datasize=4;
                        break;
                    case 8:
                        datasize=8;
                        break;
                    case 9:
                        datasize=1;
                        break;
                    default:
                        datasize=2;
                        break;
                }
                break;
            case 13:
                num=Caculation(length, value, ENDIAN);
                str=String.valueOf(num);
                break;
            case 18:
                break;
            case 14:
                break;
            case 1:
                if(value[0]==0)
                    str = "Big-Endian";
                else
                    str="Little-Endian";

                ENDIAN=value[0];
                break;
            case 7:
                break;
            case 64:
                for(int i=0;i<length;i++)
                    str1[i]=(char)value[i];

                str=String.valueOf(str1);
                break;
            case 23:
                for(int i=0;i<length;i++)
                    str1[i]=(char)value[i];

                str=String.valueOf(str1);
                break;
            case 65:
                break;
            case 21:
                break;
            case 22:
                for(int i=0;i<length;i++)
                    str1[i]=(char)value[i];

                str=String.valueOf(str1);
                break;
            case 2:
                break;
            case 3:
                break;
            case 17:
                break;
            case 15:
                break;
            case 66:
                break;
            case 67:
                break;
            case 103:
                break;
            case 136:
                break;
            case 69:
                break;
            case 70:
                break;
            case 129:
                for(int i=0;i<length;i++)
                    str1[i]=(char)value[i];

                str=String.valueOf(str1);
                break;
            case 130:
                for(int i=0;i<length;i++)
                    str1[i]=(char)value[i];

                str=String.valueOf(str1);
                break;
            case 131:
                if(ENDIAN==0){
                    years=value[0];
                    days=value[1]*256+value[2];
                    year=value[3]*256+value[4];
                    month=value[5];
                    day=value[6];
                }
                else{
                    years=value[6];
                    days=value[5]*256+value[4];
                    year=value[3]*256+value[2];
                    month=value[1];
                    day=value[0];
                }
                str=String.valueOf(year)+"."+String.valueOf(month)+"."+String.valueOf(day)+":"+String.valueOf(years)+"Age";
                break;
            case 132:
                if(value[0]==0)
                    str="Unclear";
                else if(value[0]==1)
                    str="Male";
                else if(value[0]==2)
                    str="Female";
                else
                    str="Undefined";
                break;
            case 133:
                if(ENDIAN==0){
                    year=value[0]*256+value[1];
                    month=value[2];
                    day=value[3];
                    hour=value[4];
                    minute=value[5];
                    second=value[6];
                    millsecond=value[7]*256+value[8];
                    microsecond=value[9]*256+value[10];
                }
                else{
                    year=value[10]*256+value[9];
                    month=value[8];
                    day=value[7];
                    hour=value[6];
                    minute=value[5];
                    second=value[4];
                    millsecond=value[3]*256+value[2];
                    microsecond=value[1]*256+value[0];
                }
                str=String.valueOf(year)+"."+String.valueOf(month)+"."+String.valueOf(day)+"."+String.valueOf(hour)+"."+String.valueOf(minute)+".";
                break;
            case 134:
                break;
            default:
                break;
        }

        return str;
    }

    private String TagString(int tag)
    {
        switch (tag)
        {
            case 64:
                return "Preface";
            case 5:
                return "Channels";
            case 11:
                return "Sampling rate";
            case 12:
                return "Resolution";
            case 4:
                return "Data Block";
            case 6:
                return "Sequence";
            case 8:
                return "WaveType";
            case 9:
                return "Properties";
            case 30:
                return "Wave Data";
            case 63:
                return "Channel Number";
            case 10:
                return "WaveData Type";
            case 13:
                return "Offset";
            case 18:
                return "Null";
            case 14:
                return "Compression";
            case 1:
                return "Endian";
            case 7:
                return "Pointer";
            case 23:
                return "Manufacturer";
            case 65:
                return "Event";
            case 21:
                return "Wave Info";
            case 22:
                return "Remark";
            case 2:
                return "Version";
            case 3:
                return "Character Code";
            case 17:
                return "Filter";
            case 15:
                return "Interpolation";
            case 66:
                return "Measure";
            case 67:
                return "Sampling Asymmetric";
            case 103:
                return "Group";
            case 136:
                return "Knowledge Map";
            case 69:
                return "Indicators";
            case 70:
                return "Digital Sign";
            case 129:
                return "Patient Name";
            case 130:
                return "Patient ID";
            case 131:
                return "Age";
            case 132:
                return "Patient Sex";
            case 133:
                return "Measurement";
            case 134:
                return "Message";
            default:
                return "Empty";
        }
    }

    private String DataType(int value) {
        switch (value) {
            case 0:
                return "Signed 16bits integer";
            case 1:
                return "Unsigned 16bits integer";
            case 2:
                return "Signed 32bits integer";
            case 3:
                return "Unsigned 8bits integer";
            case 4:
                return "16bits status";
            case 5:
                return "Signed 8bits integer";
            case 6:
                return "Unsigned 32bits integer";
            case 7:
                return "32bits singled-precision floating";
            case 8:
                return "64bits double-precision floating";
            case 9:
                return "8bits AHA differential";
            default:
                return "Undefinition";
        }
    }

    private String WaveType(int value)
    {
        switch (value)
        {
            case 0:
                return "Unidentified";
            case 1:
                return "Standard 12 lead ECG";
            case 2:
                return "Long-term ECG";
            case 3:
                return "Vectorcardiogram";
            case 4:
                return "Stress ECG";
            case 5:
                return "Intracardiac ECG";
            case 6:
                return "Body surface ECG";
            case 7:
                return "Ventricular late potential";
            case 8:
                return "Body surface late potential";
            case 30:
                return "PCG etc";
            case 31:
                return "Fingertip pulse, carotid pulse";
            case 20:
                return "Long-term waveform";
            case 21:
                return "Sampled waveform";
            case 25:
                return "Power spectrum";
            case 26:
                return "Trendgram";
            case 100:
                return "MCG";
            case 40:
                return "Resting EEG";
            case 41:
                return "Evoked EEG";
            case 42:
                return "Frequency analysis";
            case 43:
                return "Long-term EEG";
            default:
                return "Private";
        }
    }

    private String WaveAtt(int value)
    {
        switch (value)
        {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "V1";
            case 4:
                return "V2";
            case 5:
                return "V3";
            case 6:
                return "V4";
            case 7:
                return "V5";
            case 8:
                return "V6";
            case 9:
                return "V7";
            case 11:
                return "V3R";
            case 12:
                return "V4R";
            case 13:
                return "V5R";
            case 14:
                return "V6R";
            case 15:
                return "V7R";
            case 61:
                return "III";
            case 62:
                return "aVR";
            case 63:
                return "aVL";
            case 64:
                return "aVF";
            case 66:
                return "V8";
            case 67:
                return "V9";
            case 68:
                return "V8R";
            case 69:
                return "V9R";
            default:
                return "Undef";
        }
    }

    int Caculation(int length,int[] vaule,int endian)
    {
        int result=0;
        int binary=1;

        if(endian==0) {
            for (int i = length - 1; i >= 0; i--) {
                result += vaule[i] * binary;
                binary *= BYTESIZE;
            }
        }
        else{
            for (int i = 0; i <length; i++) {
                result += vaule[i] * binary;
                binary *= BYTESIZE;
            }
        }
        return result;
    }

    int Caculation(int length,int[] vaule,int endian,int index)
    {
        int result=0;
        int binary=1;

        if(endian==0) {
            for (int i = length - 1; i >= index; i--) {
                result += vaule[i] * binary;
                binary *= BYTESIZE;
            }
        }
        else{
            for (int i = index; i <length; i++) {
                result += vaule[i] * binary;
                binary *= BYTESIZE;
            }
        }
        return result;
    }
}

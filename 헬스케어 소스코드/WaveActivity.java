package kimsunho.medicalsignalendcoderapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButton;

import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by kimsunho on 2016-05-04.
 */

public class WaveActivity extends Activity {

    public static Context context;

    /*   위젯 변수   */
    HorizontalScrollView waveviewer;              // 파형출력 뷰
    Gallery channelListView;                     // 채널 별 리스트 뷰 출력
    ChannelListAdapter channelListAdapter;      // 채널 리스트 뷰 어댑터
    FrameLayout gridLayout;                      // 파형크기 출력 뷰
    ZoomButton zoomInButton;                    // 줌 인 버튼
    ZoomButton zoomOutButton;                   // 줌 아웃 버튼

    /*   파형 데이터 처리 및 Viewer 변수    */
    ArrayList<Integer> wavedataList;             // 파형 데이터 저장
    double[] waveViewSizeList;                  // y 축 사이즈 저장
    int mblock=0;                                 // 파형 데이터 블록 사이즈
    int msequence=0;                             // 파형 데이터 시퀀스
    int mchannel=0;                              // 파형 데이터 채널 수
    int mindex=0;                                // 파형 데이터 위치 변경
    int realtimeindex=0;                        // 실시간 데이터 저장하기위한 데이터 위치
    int waveViewSizePosition=4;                // 뷰사이즈 설정
    double mWaveSize=1.0;                       // 사이즈 조정
    boolean REALTIMESTART=false;              // 블루투스 통신 시작을 알리는 플래그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waveview);

        context=this;

        /*   위젯 아이디 설정   */
        waveviewer=(HorizontalScrollView)findViewById(R.id.waveviewer);
        waveviewer.addView(new WaveGraphicView(this));
        gridLayout=(FrameLayout)findViewById(R.id.gridlayout);
        channelListView=(Gallery)findViewById(R.id.channellistview);
        zoomInButton=(ZoomButton)findViewById(R.id.zoominButton);
        zoomOutButton=(ZoomButton)findViewById(R.id.zoomoutButton);

        channelListAdapter=new ChannelListAdapter(this);

        waveViewSizeList=new double[9];         // 사이즈 저장
        wavedataList=new ArrayList<Integer>();    // 초기화 변수

        /*   사이즈 지정   */
        waveViewSizeList[0]=5;
        waveViewSizeList[1]=4;
        waveViewSizeList[2]=3;
        waveViewSizeList[3]=2;
        waveViewSizeList[4]=1;
        waveViewSizeList[5]=0.5;
        waveViewSizeList[6]=0.3;
        waveViewSizeList[7]=0.25;
        waveViewSizeList[8]=0.2;

        gridLayout.addView(new WaveGridView(this));
        /*   갤러리의 하나의 아이템 클릭했을 때 이벤트   */
        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mindex=(position)*mblock;               // 파형 그릴 곳 초기화
                waveviewer.removeAllViews();            // 뷰어를 제거 후
                waveviewer.addView(new WaveGraphicView(context));       // 다시 그리기
            }
        });
        /*   확대 버튼 눌렀을 때 이벤트   */
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(waveViewSizePosition!=0)         // 포지션을 넘지 않기 위한 조건문
                {
                    mWaveSize=waveViewSizeList[--waveViewSizePosition];         // 사이즈를 불러 옴
                    waveviewer.removeAllViews();        // 뷰어 제거
                    gridLayout.removeAllViews();        // 뷰어 제거
                    waveviewer.addView(new WaveGraphicView(context));       // 다시 그리기
                    gridLayout.addView(new WaveGridView(context));          // 다시 그리기
                }
            }
        });
        /*   축소 버튼 눌렀을 때 이벤트   */
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(waveViewSizePosition!=waveViewSizeList.length-1)
                {
                    mWaveSize=waveViewSizeList[++waveViewSizePosition];
                    waveviewer.removeAllViews();
                    gridLayout.removeAllViews();
                    waveviewer.addView(new WaveGraphicView(context));
                    gridLayout.addView(new WaveGridView(context));
                }
            }
        });
    }

    /*   파형 데이터 저장 함수   */
    /*   순서대로 엔디안, 블락크기, 채널 수, 반복 수, 파형 시작 인덱스, 데이터 타입, 파형 속성, 파일 이름   */
    public void waveViewer(int endian,int block,int channel,int sequence,int waveindex,int type,String[] waveatt,String filename){

        mblock=block;                       // 블락저장
        msequence=sequence;                 // 시퀀스 저장
        mchannel=channel;                   // 채널 수 저장

        /*   데이터 저장 변수   */
        int data1;
        int data2;
        int data3;
        int data4;

        int dataresult=0;  //최종값 저장하는 변수

        wavedataList.clear();   // 파형 데이터 초기화
        channelListAdapter.clear();   // 채널 리스트 초기화
        waveviewer.removeAllViews();   // Wave뷰 초기화

        try{
            FileInputStream inFs=new FileInputStream(filename);         //스트림 생성
            inFs.skip(waveindex);           //파형 데이터 쪽으로 데이터 스킵

            /*   파형 데이터의 총 크기 : sequence*channelcount*blocksize MFER문서 참조  */

            for (int k=0;k<sequence;k++) {                  // 시퀀스 반복
                for (int i = 0; i < channel; i++) {         // 채널 반복
                    for (int j = 0; j < block; j++) {       // 블락 반복
                        if(type==0)
                        {
                            if (endian == 0) {
                                data1 = inFs.read();
                                data2 = inFs.read();

                                if (data1 >= 128) {
                                    dataresult = data1 * 256 + data2;
                                    dataresult = dataresult ^ 65535;
                                    dataresult++;
                                    dataresult = ~dataresult;

                                }
                                else
                                    dataresult = data1 * 256 + data2;

                            }
                            else
                            {
                                data1 = inFs.read();
                                data2 = inFs.read();

                                if (data2 >= 128)
                                {
                                    dataresult = data2 * 256 + data1;
                                    dataresult = dataresult ^ 65535;
                                    dataresult++;
                                    dataresult = (-1) * dataresult;
                                }
                                else
                                    dataresult = data2 * 256 + data1;

                            }
                        }
                        else if(type==1)
                        {
                            data1 = inFs.read();
                            data2 = inFs.read();

                            if(endian==0)
                            {
                                dataresult = data1 * 256 + data2;
                            }
                            else
                                dataresult = data2 * 256 + data1;
                        }
                        else if(type==2)                                            //데이터 크기가 너무큼 -> 나중에 수정
                        {
                            data1 = inFs.read();
                            data2 = inFs.read();
                            data3 = inFs.read();
                            data4 = inFs.read();
                        }
                        else if(type==3)
                        {
                            data1 = inFs.read();
                            dataresult=data1;
                        }
                        else if(type==4)
                        {
                            data1 = inFs.read();
                            data2 = inFs.read();
                        }
                        else if(type==5)
                        {
                            data1 = inFs.read();

                            if(data1>128)
                            {
                                data1=256-data1;
                                data1=-data1;
                            }

                            dataresult=data1;
                        }
                        wavedataList.add(dataresult);
                    }
                }
            }

        }catch (Exception e){
            Log.e("File Error",e.getMessage());
        }

        for(int i=0;i<waveatt.length;i++)
            channelListAdapter.addItem(String.valueOf(i+1),waveatt[i]);

        channelListView.setAdapter(channelListAdapter);
        waveviewer.addView(new WaveGraphicView(this));
    }

    void realTimeWaveviewer(int data)
    {
        REALTIMESTART=true;
        wavedataList.add(data);
        realtimeindex++;

        waveviewer.removeAllViews();
        waveviewer.addView(new WaveGraphicView(this));
    }
    /*   실질적인 파형자료 출력하는 클래스   */
    class WaveGraphicView extends View {

        public WaveGraphicView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
            DashPathEffect dashPath = new DashPathEffect(new float[]{10,10}, 2);

            Paint paint=new Paint();
            Paint paint1=new Paint();
            Path path=new Path();
            Paint stroke=new Paint();

            paint.setAntiAlias(true);
            paint.setColor(Color.GREEN);
            paint.setDither(true);                       // enable dithering
            paint.setStyle(Paint.Style.STROKE);         // set to STOKE
            paint.setStrokeJoin(Paint.Join.ROUND);      // set the join to round you want
            paint.setStrokeCap(Paint.Cap.ROUND);        // set the paint cap to round too
            paint.setStrokeWidth(2);

            stroke.setAntiAlias(true);
            stroke.setPathEffect(dashPath);
            stroke.setColor(Color.parseColor("#A03DB7CC"));
            stroke.setStrokeWidth(1);

            paint1.setAntiAlias(true);
            paint1.setColor(Color.WHITE);
            paint1.setTextSize(25);

            int num=30;
            int number=0;
            int x=30;
            int index=mindex;

            for(int i=0;i<13;i++)
            {
                canvas.drawLine(30,num,10000,num,stroke);
                num+=50;
            }

            num=30;
            /*   Y축 텍스트 그리기   */

            for(int j=0;j<100;j++)
            {
                canvas.drawText(String.valueOf(number),num-20,680,paint1);
                canvas.drawLine(num,30,num,620,stroke);
                number+=100;
                num+=100;
            }

            path.moveTo(30,330);

            if(REALTIMESTART)                   // 블루투스 통신을 통한 실시간 그래프 출력
            {
                int temp=realtimeindex/600;                     // 몫
                if(x==580)
                    x=30;                                          // 디바이스 전체 크기까지 파형을 실시간으로 그리기 위한 것

                for(int i=temp*600;i<realtimeindex;i++)         // 디바이스 전체 크기까지 그리기 위한 것
                {
                    path.lineTo(x, (330) - (int) (wavedataList.get(i) * mWaveSize));
                    x++;
                }
            }
            else {                  // 파일 읽어서 출력
                for (int i = 0; i < msequence; i++) {
                    for (int j = 0; j < mblock; j++) {
                        path.lineTo(x, (330) - (int) (wavedataList.get(index) * mWaveSize));
                        x++;
                        index++;
                    }
                    index += (mchannel - 1) * mblock;
                }
            }

            canvas.drawPath(path, paint);
        }

        @Override
        protected void onMeasure(int wMS, int hMS) {
            if(msequence*mblock!=0)
                setMeasuredDimension(msequence*mblock,hMS);         // 파일을 읽었을 경우
            else
                setMeasuredDimension(600,hMS);  // 초기상태
        }
    }
    /*   데이터의 크기를 출력하는 클래스   */
    class WaveGridView extends View
    {
        public WaveGridView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);

            Paint paint=new Paint();

            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setTextSize(30);

            int num=300;
            int y=50;

            /*   파일 읽기 초기에 Index오류 때문   */
            if(wavedataList.size()!=0)
                for(int i=0;i<13;i++) {
                    canvas.drawText(String.valueOf(num/mWaveSize), 10, y, paint);
                    y+=50;
                    num-=50;
                }
            else
                for(int i=0;i<13;i++) {
                    canvas.drawText(String.valueOf(num), 30, y, paint);
                    y+=50;
                    num-=50;
                }
        }
        @Override
        protected void onMeasure(int wMS, int hMS) {
            setMeasuredDimension(wMS,hMS);
        }
    }
}

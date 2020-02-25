package kimsunho.medicalsignalendcoderapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
                /*   Setting 버튼 눌렀을 때 나오는 다이얼로그 -> 헤더를 세팅 하기위한 다이얼로그   */
public class DialogActivity extends Dialog{

    /*   위젯 변수   */
    Button bConfirm;                // 데이터 저장 버튼
    Button bRemove;                 // 데이터 삭제 버튼
    EditText editText;              // 데이터 기록 에디트 텍스트
    EditText tagEditText;           // 태그값 보여주는 에디트 박스
    ExpandableListView mainListView;  // 확장 리스트 뷰

    static public Context context;          // 해당 액티비티의 정보를 저장

    /*   데이터 계산하고 저장하기 위한 변수   */
    ArrayList<String> mainList;                 //메인 리스트
    HashMap<String,ArrayList<String>> subList;          // 서브 리스트
    String mainString="";          //메인 -> 서브 눌렀을때 해쉬테이블에서 값을 얻어오기 위한것
    String subString="";            //보조 스티링
    int mpostion;                   // 눌린 위치
    boolean CHANGE=false;         // Change 다이얼로그일때

   public DialogActivity(final Context context){
        super(context);
        setContentView(R.layout.activity_dialogview);               // 해당 액티비티 View
        this.context=context;                   // 액티비티 정보 저장

       /*   위젯 아이디 지정   */
       bConfirm=(Button)findViewById(R.id.confrimbutton);
       bRemove=(Button)findViewById(R.id.removebutton);
       editText=(EditText)findViewById(R.id.valueedittext);
       tagEditText=(EditText)findViewById(R.id.tagedittext);
       mainListView=(ExpandableListView)findViewById(R.id.expandablelistview);

       /*   리스트 초기화   */
       mainList=new ArrayList<String>();
       subList=new HashMap<String,ArrayList<String>>();

       DefaultData();               //  데이터 초기화

       mainListView.setAdapter(new dialoglisviewAdapter(context, mainList,subList));            // 어댑터 설정

       /*   메인 리스트 눌렀을 때 이벤트  */
       mainListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
           @Override
           public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
               editText.setText("");                        // 에디트 위젯 텍스트 설정
               mainString=mainList.get(groupPosition);     // 누른 위치의 스트링 값을 얻어옴
               tagEditText.setText(mainString);           // 태그 에디트에 누른 위치의 스트링 저장
               return false;
           }
       });

       /*   서브 리스트 눌렀을 때 이벤트   */
       mainListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
           @Override
           public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
               subString=subList.get(mainString).get(childPosition);            // 누른 위치의 스트링 값을 얻어옴
               editText.setText(subString);             // 서브리스트 에디트 텍스트 저장

               return false;
           }
       });

       /*   확인 버튼 눌렀을 때 이벤트   */
       bConfirm.setOnTouchListener(new View.OnTouchListener() {                   //확인버튼
            @Override
            public boolean onTouch(View v, MotionEvent event) {                   //확인 버튼
                ((InfoActivity) InfoActivity.context).RenewalList(CHANGE,mpostion,mainString,editText.getText().toString());            // 확인 버튼 눌렀을 때 데이터를 Info리스트 뷰에 넣기 위한 메소드

                dismiss();              // 다이얼로그 종료
                return true;
            }
        });
        /*   삭제 버튼 눌렀을 때 이벤트   */
       bRemove.setOnTouchListener(new View.OnTouchListener() {                   //확인버튼
           @Override
           public boolean onTouch(View v, MotionEvent event) {                   //삭제 버튼
               ((InfoActivity)InfoActivity.context).RemoveList(mpostion);           // 삭제 메소드 호출

               dismiss();           // 다이얼로그 종료
               return true;
           }
       });
    }
    /*   리스트 뷰 초기화 작업   */
    private void DefaultData()
    {
        /*   서브 리스트 저장하기 위한 변수   */
        ArrayList<String> arrayList1=new ArrayList<>();
        ArrayList<String> arrayList2=new ArrayList<>();
        ArrayList<String> arrayList3=new ArrayList<>();
        ArrayList<String> arrayList4=new ArrayList<>();
        ArrayList<String> arrayList5=new ArrayList<>();
        ArrayList<String> arrayList6=new ArrayList<>();
        ArrayList<String> arrayList7=new ArrayList<>();

        /*   태그 값 스트링 값 저장 - 위치에 따라서   */
        mainList.add("Preface");        //서문
        mainList.add("Channels");       //채널 수
        mainList.add("Sampling rate"); // 표본 추출 비율
        mainList.add("Resolution");     // 표본 추출 해상도
        mainList.add("Data Block");     // 데이터 블록수
        mainList.add("Sequence");       // 반복수
        mainList.add("WaveType");       // 파형 타입
        mainList.add("Properties");     // 파형 속성
        mainList.add("WaveData");       // 파형자료
        mainList.add("Channel Number");// 채널 수
        mainList.add("WaveData Type"); // 파형 데이터 타입
        mainList.add("Offset");         // 오프셋 저장
        mainList.add("Null");           // 널 값
        mainList.add("Compression");    // 압축
        mainList.add("Endian");         // 엔디안
        mainList.add("Pointer");        // 지시자
        mainList.add("Manufacturer");   // 디바이스 제조사
        mainList.add("Event");          // 이벤트
        mainList.add("Wave Info");      // 파형 정보
        mainList.add("Remark");         // 주석
        mainList.add("Version");        // 버전
        mainList.add("Character Code"); // 문자 코드
        mainList.add("Filter");         // 필터 값
        mainList.add("Interpolation"); // 보간법
        mainList.add("Measure");        // 값 측정치
        mainList.add("Sampling Asymmetric");    // 표본 추출 비대칭
        mainList.add("Group");          // 그룹 정의
        mainList.add("Knowledge Map");  // 기술 맵
        mainList.add("Indicators");     // 참조 지시자
        mainList.add("Digital Sign");   // 디지털 사인
        mainList.add("Patient Name");    // 환자 이름
        mainList.add("Patient ID");     // 환자 아이디
        mainList.add("Age");              // 환자 나이
        mainList.add("Patient Sex");    // 환자 성별
        mainList.add("Measurement");     // 측정일자
        mainList.add("Message");          // 메시지

        /*    표본 추출 해상도 단위 저장   */
        arrayList1.add("Hz");
        arrayList1.add("Sec");
        arrayList1.add("m");
        subList.put(mainList.get(2),arrayList1);

        /*    파형 타입 저장장   */
        arrayList2.add("Unidentified");
        arrayList2.add("Standard 12 lead ECG");
        arrayList2.add("Long-term ECG");
        arrayList2.add("Vectorcardiogram");
        arrayList2.add("Stress ECG");
        arrayList2.add("Intracardiac ECG");
        arrayList2.add("Body surface ECG");
        arrayList2.add("Ventricular late potential");
        arrayList2.add("Body surface late potential");
        arrayList2.add("user defined");
        subList.put(mainList.get(6),arrayList2);

        /*   체널별 파형 속성 저장   */
        arrayList3.add("undef");
        arrayList3.add("I");
        arrayList3.add("II");
        arrayList3.add("V1");
        arrayList3.add("V2");
        arrayList3.add("V3");
        arrayList3.add("V4");
        arrayList3.add("V5");
        arrayList3.add("V6");
        arrayList3.add("V7");
        arrayList3.add("V3R");
        arrayList3.add("V4R");
        arrayList3.add("V5R");
        arrayList3.add("V6R");
        arrayList3.add("V7R");
        arrayList3.add("III");
        arrayList3.add("aVR");
        arrayList3.add("aVL");
        arrayList3.add("aVF");
        arrayList3.add("V8");
        arrayList3.add("V9");
        arrayList3.add("V8R");
        arrayList3.add("V9R");
        arrayList3.add("user defined");
        subList.put(mainList.get(7),arrayList3);

        /*   파형 데이터 타입 저장   */
        arrayList4.add("Signed 16bits integer");
        arrayList4.add("Unsigned 16bits integer");
        arrayList4.add("Signed 32bits integer");
        arrayList4.add("Unsigned 8bits integer");
        arrayList4.add("16bits status");
        arrayList4.add("Signed 8bits integer");
        arrayList4.add("Unsigned 32bits integer");
        arrayList4.add("32bits Single-precision floating");
        arrayList4.add("64bits double-precision floating");
        arrayList4.add("8bits AHA differential");
        subList.put(mainList.get(10),arrayList4);

        /*   엔디안 구조 저장   */
        arrayList5.add("Big-Endian");
        arrayList5.add("Little-Endian");
        subList.put(mainList.get(14),arrayList5);

        /*   환자 성별 저장   */
        arrayList6.add("Unclear");
        arrayList6.add("Male");
        arrayList6.add("Female");
        arrayList6.add("Undef");
        subList.put(mainList.get(33),arrayList6);

        /*   표본 추출 해상도 단위 저장   */
        arrayList7.add("V");
        arrayList7.add("uV");
        subList.put(mainList.get(3),arrayList7);
   }

    /*   Info리스트 뷰의 정보를 클릭 했을 때 발생하는 메소드   */
    public void ListViewClickEvent(String tag, String value)
    {
        mainString=tag;                             // 태그값 저장
        subString=value;                            // 서브 값 저장
        tagEditText.setText(mainString);          // 태그의 내용
        editText.setText(subString);               // 에디터 내용
    }
}

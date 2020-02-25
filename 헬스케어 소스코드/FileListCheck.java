package kimsunho.medicalsignalendcoderapplication;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*   해당 폴더의 파일들을 확인하는 클래스   */
public class FileListCheck {

    ArrayList<String> files;            // 파일 리스트를 저장하기 위한 배열 리스트

    String dirPath;                 // 디렉터리 Path 저장

    /*   파일리스트 초기화 작업   */
    public FileListCheck()
    {
        dirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() +"/files"; // 디렉터리 위치 -> 생성
        File file = new File(dirPath);                  // 절대경로의 파일 생성

        files=new ArrayList<String>();                  // 초기화

        if(!file.exists())                      // 해당위치에 폴더가 없을시 생성
            file.mkdirs();                       // 폴더 생성

        String[] fileName=file.list();                     // 파일 목록들을 읽어와서 저장

        for(int i=0;i<fileName.length;i++)     // 순차적으로 접근
            files.add(fileName[i]);             // 파일리스트 순차적으로 저장
    }
}

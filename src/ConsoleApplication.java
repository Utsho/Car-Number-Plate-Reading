import java.io.*;
import java.util.Scanner;

/**
 * Created by Mahathir on 6/17/17.
 */
public class ConsoleApplication {

    public static void main(String[] args) {
        Runtime runtime=Runtime.getRuntime();
        Scanner scanner=new Scanner(System.in);
        while(true){
            String filePath=scanner.nextLine();
            if(filePath.equals("exit")) break;
            try {
                Process imageProcessing=runtime.exec("./run.sh "+filePath);
                BufferedReader cmdOutput=new BufferedReader(new InputStreamReader(imageProcessing.getInputStream()));
                BufferedReader cmdError=new BufferedReader(new InputStreamReader(imageProcessing.getErrorStream()));
                String str;
                while((str=cmdOutput.readLine())!=null){
                    System.out.println(str);
                }
                while((str=cmdError.readLine())!=null){
                    System.err.println(str);
                }
                //imageProcessing.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader output=new BufferedReader(new InputStreamReader(new FileInputStream("py/output.txt")));
                String str;
                while((str=output.readLine())!=null){
                    System.out.println(str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

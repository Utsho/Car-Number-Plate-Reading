import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Mahathir on 6/17/17.
 */
public class GuiApplication extends Application {
    String filePath;
    HashMap<String,String> hashMap=new HashMap<>();
    HashMap<String,String> status=new HashMap<>();
    List<String> fileIndexList=new ArrayList<>();
    Runtime runtime;
    public static void main(String[] args) {

        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        runtime=Runtime.getRuntime();
        VBox vBox=new VBox();
        HBox hBox=new HBox();
        Text label=new Text();
        label.setFill(Color.BLUE);
        label.setFont(Font.loadFont("file:font/kalpurush.ttf",36));
        HBox optionButton=new HBox();
        Button openFile=new Button("OpenFile");
        Button openDir=new Button("Open Directory");
        ProgressBar progressBar=new ProgressBar();
        progressBar.setProgress(0);

        progressBar.setMinWidth(200);
        optionButton.getChildren().addAll(openFile,openDir,progressBar);
        ImageView original=new ImageView();
        hBox.getChildren().addAll(original,label);
        Button start=new Button("Start");
        ListView<String> step1=new ListView<>();
        step1.setOrientation(Orientation.HORIZONTAL);
        step1.setMinHeight(180);
        ListView<String> step2=new ListView<>();
        step2.setOrientation(Orientation.HORIZONTAL);
        step2.setMinHeight(60);
        HBox hBox1=new HBox();
        vBox.getChildren().addAll(optionButton,hBox,start,step1,step2,hBox1);
        original.setFitWidth(500);
        original.setFitHeight(380);
        Button step1_inter=new Button("Step1 Hidden Image");
        Button step2_inter=new Button("Step2 Hidden Image");
        Button angle_image=new Button("Radon Transform Image");
        hBox1.getChildren().addAll(step1_inter,step2_inter,angle_image);

        openFile.setOnAction(e->{
            FileChooser fileChooser=new FileChooser();
            File file=fileChooser.showOpenDialog(primaryStage);
            filePath=file.getAbsolutePath();
            Image image=new Image("file:///"+filePath);
            original.setImage(image);
        });
        step2.setCellFactory((ListView<String> listView) -> new ListCell<String>() {



                public void updateItem(String str, boolean empty) {
                    if(empty){
                        setText(null);
                        setGraphic(null);
                    }
                    else{
                        VBox vBox=new VBox();
                        Label label=new Label();
                        ImageView imageView = new ImageView();
                        Image image=new Image(str);
                        imageView.setImage(image);
                        imageView.setFitWidth(80);
                        imageView.setFitHeight(50);
                        label.setText(hashMap.get(str));
                        label.setFont(new Font(18));
                        vBox.getChildren().addAll(imageView,label);
                        setGraphic(vBox);
                    }

                }
        });

        step1.setCellFactory((ListView<String> listView) -> new ListCell<String>() {
            private final ImageView imageView = new ImageView();

            public void updateItem(String str, boolean empty) {
                if(empty){
                    setText(null);
                    setGraphic(null);
                }
                else if(status.get(str).equals("can_not_reject")){
                    Image image=new Image(str);
                    imageView.setImage(image);
                    imageView.setFitWidth(400);
                    imageView.setFitHeight(160);
                    setGraphic(imageView);
                    //setText(status.get(str));
                }

            }
        });






        /*Task Properties*/

        Task singleFileTask=new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    updateProgress(0,100);
                    System.out.println("Processing image:"+filePath);
                    long time0=System.nanoTime();
                    Process imageProcessing=runtime.exec("./clean.sh "+filePath);
                    imageProcessing.waitFor();
                    updateProgress(1,100);
                    long time1=System.nanoTime();
                    imageProcessing=runtime.exec("./detect.sh "+filePath);
                    imageProcessing.waitFor();
                    long time2=System.nanoTime();
                    updateProgress(36,100);
                    imageProcessing=runtime.exec("./radon_transform.sh");
                    imageProcessing.waitFor();
                    long time3=System.nanoTime();
                    updateProgress(50,100);
                    imageProcessing=runtime.exec("./segment.sh");
                    imageProcessing.waitFor();
                    long time4=System.nanoTime();
                    updateProgress(51,100);
                    imageProcessing=runtime.exec("./ocr.sh");
                    imageProcessing.waitFor();
                    long time5=System.nanoTime();
                    updateProgress(99,100);


                    long totalTime=time5-time0;
                    System.out.println("Total time         :"+totalTime/1000000000.0+" s");
                    System.out.println("Cleaning Time      :"+(time1-time0)/1000000000.0+" s "+(double)(time1-time0)*100.0/(double)totalTime+"%");
                    System.out.println("LP Detect Time     :"+(time2-time1)/1000000000.0+" s "+(double)(time2-time1)*100.0/(double)totalTime+"%");
                    System.out.println("Radon Transform    :"+(time3-time2)/1000000000.0+" s "+(double)(time3-time2)*100.0/(double)totalTime+"%");
                    System.out.println("Segment Time       :"+(time4-time3)/1000000000.0+" s "+(double)(time4-time3)*100.0/(double)totalTime+"%");
                    System.out.println("Tensorflow OCR Time:"+(time5-time4)/1000000000.0+" s "+(double)(time5-time4)*100.0/(double)totalTime+"%");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        long time5=System.nanoTime();
                        try {

                            BufferedReader output=new BufferedReader(new InputStreamReader
                                    (new FileInputStream("py/output.txt")));
                            String str;
                            List<String> stringList=new ArrayList<>();
                            stringList.clear();
                            while((str=output.readLine())!=null){
                                stringList.add(str);
                                //System.out.println(str);
                            }

                            BufferedReader fileIndex=new BufferedReader(new InputStreamReader
                                    (new FileInputStream("py/input_index.txt")));
                            int i=0;

                            while((str=fileIndex.readLine())!=null){
                                File file=new File("py/final/"+str);
                                fileIndexList.add("file:///"+file.getAbsolutePath());

                                hashMap.put("file:///"+file.getAbsolutePath(),stringList.get(i++));
                                // step2.getItems().add("file:///"+file.getAbsolutePath());
                            }



                            //System.out.println("\nBefore Sort\n");
                            for(String str2:fileIndexList) System.out.println(str2);
                            fileIndexList.sort(new Comparator<String>() {
                                @Override
                                public int compare(String o1, String o2) {
                                    int sm=(o1.length()<o2.length())?o1.length() : o2.length();
                                    int i;
                                    for(i=0;i<sm;i++){
                                        if (o1.charAt(i) != o2.charAt(i)) break;
                                    }
                                    return o1.charAt(i)-o2.charAt(i);
                                }
                            });
                            String finalIndex="";
                            int stIndex=fileIndexList.get(0).lastIndexOf('/');
                            int enIndex=fileIndexList.get(0).lastIndexOf("_");
                            String prevFile=fileIndexList.get(0).substring(stIndex,enIndex);
                            //System.out.println("\nAfter Sort\n");
                            for(String str2:fileIndexList){
                                //System.out.println(str2);
                                stIndex=str2.lastIndexOf('/');
                                enIndex=str2.lastIndexOf("_");
                                String currentFile=str2.substring(stIndex,enIndex);
                                if(!prevFile.equals(currentFile)) {
                                    prevFile=currentFile;
                                    finalIndex+="//";
                                }
                                finalIndex+=hashMap.get(str2)+" ";
                                step2.getItems().add(str2);
                            }
                            label.setText(finalIndex);
                            String parts[]=finalIndex.split("//");
                            boolean flag=true;
                            for(String str1:parts){
                                for(String str2:parts){
                                    if(!str1.equals(str2)) flag=false;
                                }
                            }
                            if(flag) finalIndex=parts[0];
                            label.setText(finalIndex);




                            fileIndex=new BufferedReader(new InputStreamReader
                                    (new FileInputStream("py/lp_status.txt")));
                            while((str=fileIndex.readLine())!=null){
                                String st[]=str.split("##");
                                File file=new File("py/step1_output/"+st[0]);
                                status.put("file:///"+file.getAbsolutePath(),st[1]);
                            }


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                        File step1_dir=new File("py/step1_output");
                        for(File file:step1_dir.listFiles()){
                            step1.getItems().add("file:///"+file.getAbsolutePath());

                        }

                        long time6=System.nanoTime();
                        System.out.println("UI update time:"+(time6-time5)/1000000000.0);
                    }
                });

                updateProgress(100,100);



                return null;
            }
        };
        progressBar.progressProperty().bind(singleFileTask.progressProperty());



        start.setOnAction(event->{
            step1.getItems().clear();
            step2.getItems().clear();
            fileIndexList.clear();
            hashMap.clear();
            Thread t=new Thread(singleFileTask);
            t.start();
        });
























        /*Task */










        step1_inter.setOnAction(e->{
            showIntermidiateStage("py/step1_intermidiate");
        });

        step2_inter.setOnAction(e->{
            showIntermidiateStage("py/step2_intermidiate");
        });

        angle_image.setOnAction(e->{
            showIntermidiateStage("py/angle");
        });

        Scene scene=new Scene(vBox,900,700);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    void showIntermidiateStage(String dir){
        Stage stage=new Stage();
        VBox vBox=new VBox();
        ListView<String> imageListView=new ListView<>();
        imageListView.setMinHeight(550);
        imageListView.setOrientation(Orientation.HORIZONTAL);
        Button button=new Button("Close");
        vBox.getChildren().addAll(imageListView,button);
        imageListView.setCellFactory((ListView<String> listView) -> new ListCell<String>() {
            private final ImageView imageView = new ImageView();

            public void updateItem(String str, boolean empty) {
                if(empty){
                    setText(null);
                    setGraphic(null);
                }
                else{
                    Image image=new Image(str);
                    imageView.setImage(image);
                    imageView.setFitWidth(700);
                    imageView.setFitHeight(500);
                    setGraphic(imageView);
                    setText(str.substring(str.lastIndexOf("/")));
                }

            }
        });
        File step1_dir=new File(dir);
        for(File file:step1_dir.listFiles()){
            imageListView.getItems().add("file:///"+file.getAbsolutePath());

        }
        button.setOnAction(e->{
            stage.close();
        });

        SortedList<String> sortedList=new SortedList<String>(imageListView.getItems());
        sortedList.setComparator(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int sm=(o1.length()<o2.length())?o1.length() : o2.length();
                int i;
                for(i=0;i<sm;i++){
                    if (o1.charAt(i) != o2.charAt(i)) break;
                }
                return o1.charAt(i)-o2.charAt(i);
            }
        });
        Scene scene=new Scene(vBox,700,600);
        stage.setScene(scene);
        stage.show();


    }

}

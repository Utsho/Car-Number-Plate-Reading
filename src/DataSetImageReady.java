import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mahathir on 6/21/17.
 */
public class DataSetImageReady extends Application {
    private String filePath;
    private HashMap<String,String> hashMap=new HashMap<>();
    private HashMap<String,String> status=new HashMap<>();
    private Runtime runtime;
    private List<String> fileIndexList=new ArrayList<>();
    private String dirPath="";
    private int currentSrcFile=0;
    private List<String> srcFileList=new ArrayList<>();
    private float accuracy;


    public static void main(String[] args) {
        Application.launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FileWriter fileWriter=new FileWriter(new File("log.txt"));
        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
        runtime=Runtime.getRuntime();
        final boolean[] finish = {true};
        Button next=new Button("Next");
        final int[][] currentDataIndex = {{0}};
        HBox root=new HBox();
        VBox vBox=new VBox();
        VBox sidePanel=new VBox();
        vBox.setMaxWidth(850);
        root.getChildren().addAll(vBox,sidePanel);
        Label label=new Label();
        label.setTextFill(Color.RED);
       // label.setFont(new Font(6));
        Button openFile=new Button("OpenFile");
        Button openDir=new Button("Open Directory");
        HBox optionButton=new HBox(openFile,openDir,label);
        ImageView original=new ImageView();
        Button start=new Button("Start");
        ListView<String> step1=new ListView<>();
        step1.setOrientation(Orientation.HORIZONTAL);
        ListView<String> step2=new ListView<>();
        step2.setOrientation(Orientation.HORIZONTAL);
        HBox hBox1=new HBox();
        vBox.getChildren().addAll(optionButton,original,start,step1,step2,hBox1);
        original.setFitWidth(600);
        original.setFitHeight(300);
        Button step1_inter=new Button("Step1 Hidden Image");
        Button step2_inter=new Button("Step2 Hidden Image");
        Button angle_image=new Button("Radon Transform Image");
        hBox1.getChildren().addAll(step1_inter,step2_inter,angle_image);
        Stage stage=new Stage();

        ProgressBar progressIndicator=new ProgressBar();
        ProgressIndicator progressIndicator1=new ProgressIndicator();
        progressIndicator1.setMinWidth(400);
        progressIndicator1.setMinHeight(400);
        progressIndicator.setMinWidth(400);
        Label label2=new Label();
        Label label13=new Label("0%");
        label13.setFont(new Font(96));
        VBox vBox1=new VBox(label2,label13);

        Scene scene1=new Scene(vBox1,400,600);
        stage.setScene(scene1);


        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> progressIndicator.setProgress(0));
                int i=1;
                int totalSize=srcFileList.size();
                for(String str:srcFileList){
                    int finalI1 = i;
                    Platform.runLater(() -> label2.setText("Working On :["+ finalI1 +"/"+totalSize+"] "+ str));

                    try {
                        System.out.println("Processing image:"+dirPath+"/"+str);
                        Process imageProcessing=runtime.exec("./run.sh "+dirPath+"/"+str);

                       /* BufferedReader cmdOutput=new BufferedReader(new InputStreamReader(imageProcessing.getInputStream()));
                        BufferedReader cmdError=new BufferedReader(new InputStreamReader(imageProcessing.getErrorStream()));
                        String st;

                        while((st=cmdOutput.readLine())!=null){
                            System.out.println(st);

                        }
                        while((st=cmdError.readLine())!=null){
                            System.err.println(st);
                        }*/
                        imageProcessing.waitFor();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    File file=new File(dirPath+"/output/"+str);
                    file.mkdir();
                    runtime.exec("cp -R py/angle "+dirPath+"/output/"+str).waitFor();
                    runtime.exec("cp -R py/final "+dirPath+"/output/"+str).waitFor();
                    runtime.exec("cp -R py/step1_output "+dirPath+"/output/"+str).waitFor();
                    runtime.exec("cp -R py/step1_intermidiate "+dirPath+"/output/"+str).waitFor();
                    runtime.exec("cp -R py/step2_intermidiate "+dirPath+"/output/"+str).waitFor();

                    runtime.exec("cp py/input_index.txt "+dirPath+"/output/"+str+"/input_index.txt").waitFor();
                    runtime.exec("cp py/output.txt "+dirPath+"/output/"+str+"/output.txt").waitFor();
                    runtime.exec("cp py/lp_status.txt "+dirPath+"/output/"+str+"/lp_status.txt").waitFor();


                    int finalI = i;
                    Platform.runLater(() -> {
                        double prog= (double) (finalI)/totalSize;
                        label13.setText((int)(prog*100)+"%");
                        //progressIndicator.setProgress(prog*100.0);
                    });
                    i++;

                }

                Thread.sleep(1000);

                Platform.runLater(() -> {
                    step1.getItems().clear();
                    step2.getItems().clear();
                    fileIndexList.clear();
                    hashMap.clear();
                    currentDataIndex[0][0]=0;
                    finish[0] =true;
                    currentSrcFile=0;
                    String src=srcFileList.get(currentSrcFile);
                    System.out.println("Currently Showing:"+src);
                    original.setImage(new Image("file:///"+dirPath+"/"+src));
                    try {
                        runtime.exec("./clean.sh").waitFor();
                        runtime.exec("cp -R "+dirPath+"/output/"+src+"/angle py/").waitFor();
                        runtime.exec("cp -R "+dirPath+"/output/"+src+"/final py/").waitFor();
                        runtime.exec("cp -R "+dirPath+"/output/"+src+"/step1_output py/").waitFor();
                        runtime.exec("cp -R "+dirPath+"/output/"+src+"/step1_intermidiate py/").waitFor();
                        runtime.exec("cp -R "+dirPath+"/output/"+src+"/step2_intermidiate py/").waitFor();
                        runtime.exec("cp "+dirPath+"/output/"+src+"/input_index.txt py/input_index.txt ").waitFor();
                        runtime.exec("cp "+dirPath+"/output/"+src+"/output.txt py/output.txt ").waitFor();
                        runtime.exec("cp "+dirPath+"/output/"+src+"/lp_status.txt py/lp_status.txt ").waitFor();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        BufferedReader output=new BufferedReader(new InputStreamReader
                                (new FileInputStream("py/output.txt")));
                        String str;
                        List<String> stringList=new ArrayList<>();
                        stringList.clear();
                        while((str=output.readLine())!=null){
                            stringList.add(str);
                           // System.out.println(str);
                        }

                        BufferedReader fileIndex=new BufferedReader(new InputStreamReader
                                (new FileInputStream("py/input_index.txt")));
                        int i1 =0;

                        while((str=fileIndex.readLine())!=null){
                            File file=new File("py/final/"+str);
                            fileIndexList.add("file:///"+file.getAbsolutePath());

                            hashMap.put("file:///"+file.getAbsolutePath(),stringList.get(i1++));
                            // step2.getItems().add("file:///"+file.getAbsolutePath());
                        }



                        //System.out.println("\nBefore Sort\n");
                        //for(String str2:fileIndexList) System.out.println(str2);
                        fileIndexList.sort((o1, o2) -> {
                            int sm=(o1.length()<o2.length())?o1.length() : o2.length();
                            int i11;
                            for(i11 =0; i11 <sm; i11++){
                                if(o1.charAt(i11)!=o2.charAt(i11)) break;
                            }
                            return o1.charAt(i11)-o2.charAt(i11);
                        });
                        String finalIndex="";
                        int stIndex=0;
                        int enIndex=0;
                        String prevFile=null;
                        if(fileIndexList.size()>0)
                            stIndex=fileIndexList.get(0).lastIndexOf('/');
                        if(fileIndexList.size()>0)
                            enIndex=fileIndexList.get(0).lastIndexOf("_");
                        if(fileIndexList.size()>0)prevFile=fileIndexList.get(0).substring(stIndex,enIndex);
                        //System.out.println("\nAfter Sort\n");
                        for(String str2:fileIndexList){
                            //System.out.println(str2);
                            stIndex=str2.lastIndexOf('/');
                            enIndex=str2.lastIndexOf("_");
                            String currentFile=str2.substring(stIndex,enIndex);
                            assert prevFile != null;
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
                    stage.close();
                });

                return null;
            }
        };


        openDir.setOnAction(e->{
            DirectoryChooser directoryChooser=new DirectoryChooser();
            File f=directoryChooser.showDialog(primaryStage);
            dirPath=f.getPath();
            for(File f1:f.listFiles()){
                if(f1.isDirectory()) continue;
                srcFileList.add(f1.getName());
            }
            File outputFolder=new File(dirPath+"/output");
            outputFolder.delete();
            outputFolder.mkdir();
            new Thread(task).start();
            stage.showAndWait();
        });

        openFile.setOnAction(e->{
            FileChooser fileChooser=new FileChooser();
            File file=fileChooser.showOpenDialog(primaryStage);
            filePath=file.getAbsolutePath();
            Image image=new Image("file:///"+filePath);
            original.setImage(image);
        });
        step2.setCellFactory((ListView<String> listView) -> new ListCell<String>() {
            private final ImageView imageView = new ImageView();

            public void updateItem(String str, boolean empty) {
                if(empty){
                    setText(null);
                    setGraphic(null);
                }
                else{
                    Image image=new Image(str);
                    imageView.setImage(image);
                    imageView.setFitWidth(80);
                    imageView.setFitHeight(50);
                    setText(hashMap.get(str));
                    setGraphic(imageView);
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
                else{
                    Image image=new Image(str);
                    imageView.setImage(image);
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(80);
                    setGraphic(imageView);
                    setText(status.get(str));
                }

            }
        });


        start.setOnAction(event->{
            step1.getItems().clear();
            step2.getItems().clear();
            fileIndexList.clear();
            hashMap.clear();
            currentDataIndex[0][0]=0;
            finish[0] =true;
            next.setText("Next");
            try {
                System.out.println("Processing image:"+filePath);
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader output=new BufferedReader(new InputStreamReader
                        (new FileInputStream("py/output.txt")));
                String str;
                List<String> stringList=new ArrayList<>();
                stringList.clear();
                while((str=output.readLine())!=null){
                    stringList.add(str);
                    System.out.println(str);
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



                System.out.println("\nBefore Sort\n");
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
                int stIndex=0;
                int enIndex=0;
                String prevFile=null;
                if(fileIndexList.size()>0)
                    stIndex=fileIndexList.get(0).lastIndexOf('/');
                if(fileIndexList.size()>0)
                    enIndex=fileIndexList.get(0).lastIndexOf("_");
                if(fileIndexList.size()>0)prevFile=fileIndexList.get(0).substring(stIndex,enIndex);
                //System.out.println("\nAfter Sort\n");
                for(String str2:fileIndexList){
                    System.out.println(str2);
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




        });







        step1_inter.setOnAction(e->{
            showIntermidiateStage("py/step1_intermidiate");
        });

        step2_inter.setOnAction(e->{
            showIntermidiateStage("py/step2_intermidiate");
        });

        angle_image.setOnAction(e->{
            showIntermidiateStage("py/angle");
        });

        /* side panel */
        TextField textField1=new TextField();
        CheckBox checkBox=new CheckBox("Is License Plate Detected ?");
        HBox hBox=new HBox(checkBox,textField1);

        ImageView currentDataSet=new ImageView();
        ListView<String> classesListView=new ListView<>();
        Button addToDataSet=new Button("Add to DataSet");
        HBox addnewClassPanel=new HBox();
        Label label1=new Label("Not In Classes List. Add new class");
        TextField textField=new TextField();
        Button addClass=new Button("Add Class");
        addnewClassPanel.getChildren().addAll(label1,textField,addClass);
        sidePanel.getChildren().addAll(hBox,next,currentDataSet,classesListView,
                addToDataSet,addnewClassPanel);
        next.setOnAction(event->{
            if(currentDataIndex[0][0]==0){
                try {
                    bufferedWriter.write(srcFileList.get(currentSrcFile)+":"+
                            checkBox.isSelected()+":"+textField1.getText()+"\n");
                    if (checkBox.isSelected()) accuracy+=1;
                    bufferedWriter.flush();
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(finish[0] && currentSrcFile>=srcFileList.size()-1){
                try {
                    Process imageProcessing=runtime.exec("./train.sh "+filePath);

                    BufferedReader cmdOutput=new BufferedReader(new InputStreamReader(imageProcessing.getInputStream()));
                    BufferedReader cmdError=new BufferedReader(new InputStreamReader(imageProcessing.getErrorStream()));
                    String str;

                    while((str=cmdOutput.readLine())!=null){
                        System.out.println(str);

                    }
                    while((str=cmdError.readLine())!=null){
                        System.err.println(str);
                    }
                    imageProcessing.waitFor();
                    bufferedWriter.flush();
                    fileWriter.flush();
                    fileWriter.close();
                    primaryStage.close();
                    stage.show();
                    System.out.println(accuracy/srcFileList.size());
                    label2.setText("Keep the log file");
                    progressIndicator.setVisible(false);
                    label13.setText("Good Bye");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(finish[0]  || fileIndexList.size()==0){
                step1.getItems().clear();
                step2.getItems().clear();
                fileIndexList.clear();
                hashMap.clear();
                currentDataIndex[0][0]=0;
                finish[0] =true;
                label.setText("");
                next.setText("next");


                currentSrcFile++;
                String src=srcFileList.get(currentSrcFile);
                original.setImage(new Image("file:///"+dirPath+"/"+src));
                try {
                    runtime.exec("./clean.sh").waitFor();
                    runtime.exec("cp -R "+dirPath+"/output/"+src+"/angle py/").waitFor();
                    runtime.exec("cp -R "+dirPath+"/output/"+src+"/final py/").waitFor();
                    runtime.exec("cp -R "+dirPath+"/output/"+src+"/step1_output py/").waitFor();
                    runtime.exec("cp -R "+dirPath+"/output/"+src+"/step1_intermidiate py/").waitFor();
                    runtime.exec("cp -R "+dirPath+"/output/"+src+"/step2_intermidiate py/").waitFor();
                    runtime.exec("cp "+dirPath+"/output/"+src+"/input_index.txt py/input_index.txt ").waitFor();
                    runtime.exec("cp "+dirPath+"/output/"+src+"/output.txt py/output.txt ").waitFor();
                    runtime.exec("cp "+dirPath+"/output/"+src+"/lp_status.txt py/lp_status.txt ").waitFor();

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    BufferedReader output=new BufferedReader(new InputStreamReader
                            (new FileInputStream("py/output.txt")));
                    String str;
                    List<String> stringList=new ArrayList<>();
                    stringList.clear();
                    while((str=output.readLine())!=null){
                        stringList.add(str);
                        System.out.println(str);
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



                   // System.out.println("\nBefore Sort\n");
                   // for(String str2:fileIndexList) System.out.println(str2);
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
                    StringBuilder finalIndex= new StringBuilder();
                    int stIndex=0;
                    int enIndex=0;
                    String prevFile=null;
                    if(fileIndexList.size()>0)
                        stIndex=fileIndexList.get(0).lastIndexOf('/');
                    if(fileIndexList.size()>0)
                        enIndex=fileIndexList.get(0).lastIndexOf("_");
                    if(fileIndexList.size()>0)prevFile=fileIndexList.get(0).substring(stIndex,enIndex);
                    //System.out.println("\nAfter Sort\n");;
                    for(String str2:fileIndexList){
                      //  System.out.println(str2);
                        stIndex=str2.lastIndexOf('/');
                        enIndex=str2.lastIndexOf("_");
                        String currentFile=str2.substring(stIndex,enIndex);
                        assert prevFile != null;
                        if(!prevFile.equals(currentFile)) {
                            prevFile=currentFile;
                            finalIndex.append("//");
                        }
                        finalIndex.append(hashMap.get(str2)).append(" ");
                        step2.getItems().add(str2);
                    }
                    label.setText(finalIndex.toString());
                    String parts[]= finalIndex.toString().split("//");
                    boolean flag=true;
                    for(String str1:parts){
                        for(String str2:parts){
                            if(!str1.equals(str2)) flag=false;
                        }
                    }
                    if(flag) finalIndex = new StringBuilder(parts[0]);
                    label.setText(finalIndex.toString());




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




            }
            else{
                classesListView.setItems(getAllClasses());
                Image image=new Image(fileIndexList.get(currentDataIndex[0][0]));
                currentDataSet.setImage(image);
                int select=-1;
                String vl=hashMap.get(fileIndexList.get(currentDataIndex[0][0]));
                for(int i=0;i<classesListView.getItems().size();i++){
                    String cl=classesListView.getItems().get(i);
                    if(cl.equals(vl)) {
                        select=i;
                        break;
                    }
                }
                if(select!=-1) classesListView.getSelectionModel().select(select);
            }

        });

        addToDataSet.setOnAction(e->{
            String src=fileIndexList.get(currentDataIndex[0][0]);
            src=src.substring(src.lastIndexOf("/"));
            src="py/final/"+src;
            if(!classesListView.getSelectionModel().getSelectedItem().equals("noise & skip")){
                File desDir=new File("py/datasetOriginal/"+classesListView.getSelectionModel().getSelectedItem());
                int fileIndex=desDir.listFiles().length;
                String des="py/datasetOriginal/"+classesListView.getSelectionModel().getSelectedItem()+
                        "/"+fileIndex+".png";
                String command="cp "+src+" "+des;
                System.out.println(command);
                try {
                    runtime.exec(command);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if(currentDataIndex[0][0]==fileIndexList.size()-1) {
                next.setText("Finish");
                finish[0]=true;
            }
            else{
                currentDataIndex[0][0]++;
                classesListView.setItems(getAllClasses());
                Image image=new Image(fileIndexList.get(currentDataIndex[0][0]));
                currentDataSet.setImage(image);
                int select=-1;
                String vl=hashMap.get(fileIndexList.get(currentDataIndex[0][0]));
                for(int i=0;i<classesListView.getItems().size();i++){
                    String cl=classesListView.getItems().get(i);
                    if(cl.equals(vl)) {
                        select=i;
                        break;
                    }
                }
                if(select!=-1) classesListView.getSelectionModel().select(select);
                else classesListView.getSelectionModel().select("noise & skip");
            }

        });


        addClass.setOnAction(e->{
            try {
                System.out.println("mkdir py/datasetOriginal/"+textField.getText());
                runtime.exec("mkdir py/datasetOriginal/"+textField.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            classesListView.setItems(getAllClasses());
            String vl=textField.getText();
            int select=-1;
            for(int i=0;i<classesListView.getItems().size();i++){
                String cl=classesListView.getItems().get(i);
                if(cl.equals(vl)) {
                    select=i;
                    break;
                }
            }
            if(select!=-1) classesListView.getSelectionModel().select(select);
            else classesListView.getSelectionModel().select("noise & skip");
            classesListView.getSelectionModel().select(textField.getText());

        });



        Scene scene=new Scene(root,1100,700);
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


    ObservableList<String> getAllClasses(){
        ObservableList<String> stringObservableList= FXCollections.observableArrayList();
        File file=new File("py/datasetOriginal");
        for(File f:file.listFiles()){
            stringObservableList.add(f.getName());
        }
        stringObservableList.add("noise & skip");
        return stringObservableList;
    }
}


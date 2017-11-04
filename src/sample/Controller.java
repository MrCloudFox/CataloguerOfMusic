package sample;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedList;


public class Controller {

    private static ArrayList<File> listWithFileNames = new ArrayList<>();
    private static String MyPath = null;

    @FXML
    private TextField pathField;


    public static void getListFiles(String str) {
        File f = new File(str);
        for (File s : f.listFiles()) {
            if (s.isFile()) {
                listWithFileNames.add(s);
            } else if (s.isDirectory()) {
                getListFiles(s.getAbsolutePath());
            }
        }

    }
    public void acceptPath(ActionEvent actionEvent) throws IOException{
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        MyPath = pathField.getText();
        if(MyPath == null){
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter full path of your directory with music!");

            alert.showAndWait();
        } else {
            getListFiles(MyPath);
            MyPath = MyPath.replace("\\", "/");
            createFolders();

            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Your music successfully cataloged!");

            alert.showAndWait();
        }
    }

    public static void createFolders() throws IOException {

        LinkedList<String> existingFolders = new LinkedList<>();
        LinkedList<String> catocolisedFiles = new LinkedList<>();
        Path destFile;
        Path sourceFile;
        String patternOfMusicName = null;

        FileWriter CatocolisedFiles = new FileWriter("CatocolisedMusic.txt", true);

        Files.lines(Paths.get("CatocolisedMusic.txt"), StandardCharsets.UTF_8).forEach(catocolisedFiles::add);



        try {
            for (File fil : listWithFileNames) {

            if(fil.getName().contains("-") && !catocolisedFiles.contains(fil.getName())) {
                sourceFile = FileSystems.getDefault().getPath(MyPath + "/" + fil.getName());
                if (!fil.getName().split("-")[0].substring(fil.getName().split("-")[0].length() - 1, fil.getName().split("-")[0].length()).equals(" ")) {
                    patternOfMusicName = fil.getName().split("-")[0];
                } else if (fil.getName().split(" ")[1].equals("-")) {
                    patternOfMusicName = fil.getName().split("-")[0].replace(" ", "");
                } else {
                    patternOfMusicName = fil.getName().split("-")[0].substring(0, fil.getName().split("-")[0].length() - 1);
                    boolean spaces = true;
                    int countOfSpaces = 1;
                    while (spaces) {
                        if (patternOfMusicName.substring(patternOfMusicName.length() - 1, patternOfMusicName.length()).equals(" ")) {
                            patternOfMusicName = fil.getName().split("-")[0].substring(0, fil.getName().split("-")[0].length() - countOfSpaces);
                            countOfSpaces++;
                        } else spaces = false;
                    }

                }

                if (!existingFolders.contains(patternOfMusicName)) {
                    existingFolders.add(patternOfMusicName);
                    File myPath = new File(MyPath + "/" + patternOfMusicName);
                    myPath.mkdirs();
                    destFile = FileSystems.getDefault().getPath(MyPath + "/" + patternOfMusicName + "/" + fil.getName());
                    Files.move(sourceFile, destFile);
                } else {
                    destFile = FileSystems.getDefault().getPath(MyPath + "/" + patternOfMusicName + "/" + fil.getName());
                    Files.move(sourceFile, destFile);
                }
                CatocolisedFiles.write(fil.getName());
                CatocolisedFiles.append("\r\n");

            }

            }
            CatocolisedFiles.flush();
        } catch (NoSuchFileException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

}
package com.pepper.mediaplayerone;

import model.MediaFile;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javafx.util.Duration;
import static com.pepper.mediaplayerone.DoubleClickEvent.DoubleClickEvent;
import java.util.ArrayList;
import java.util.Collections;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class PrimaryController implements Initializable
{
    @FXML
    private MediaView mv;
    @FXML
    private Button playBtn, pauseBtn, stopBtn, reBtn, forwBtn;
    @FXML
    private VBox playListVBox, vol, volMsg, root;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label v, o, l, u, m, e;
    
    
    private static double counter = 0;
    private List<File> playlist, tempList;
    public static int index = 0;
    public static int currentIndex;
    private Media media;
    private FileChooser fileChooser;
    private File selectedFile;
    private Window stage;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        currentIndex = 0;
        playlist = new ArrayList<>();
        
        root.setOnDragOver(event->{
            if(event.getGestureSource() != root && event.getDragboard().hasFiles())
            {
                event.acceptTransferModes(TransferMode.COPY);
            }
        });
        
        root.setOnDragDropped(event ->{
            Dragboard dragboard = event.getDragboard();
            if(dragboard.hasFiles())
            {
                System.out.println("van de szar");
                int index = 0;
                dragboard.getFiles().forEach(file -> {
                    playlist.add(file);
                });
                
                loadDragNDropFiles();
                event.setDropCompleted(true);
            } else {
                System.out.println("has not files");
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }
    
    public void playMedia() //play button function
    {
        if(!playlist.isEmpty()){
            selectedFile = playlist.get(currentIndex);
        }
        
        if (selectedFile != null) 
        {
            
            String mediaSource = selectedFile.toURI().toString();
            media = new Media(mediaSource);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnEndOfMedia(() -> playNextMedia());
            setScrollVolume();
            mv.setMediaPlayer(mediaPlayer);
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) ->
            {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                if (totalDuration.greaterThan(Duration.ZERO)) 
                {
                    progressBar.setProgress(newValue.toMillis() / totalDuration.toMillis());
                }
            });

            
            if (null != mediaPlayer.getStatus()) switch (mediaPlayer.getStatus())
            {
                case PAUSED:
                    mediaPlayer.seek(mediaPlayer.getCurrentTime());
                    mediaPlayer.play();
                    break;
                case STOPPED:
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.play();
                    break;
                case UNKNOWN:
                    mediaPlayer.play();
                    System.out.println("vol at start "+mediaPlayer.getVolume());
                    break;
                default:
                    break;
            }
            System.out.println(selectedFile.toURI().toString());
        }
    }

    public void pauseMedia() // pause button function
    {
        if (selectedFile != null && mediaPlayer != null) 
        {
            System.out.println("mediaPlayer.getStatus() " + mediaPlayer.getStatus());
            if (mediaPlayer.getStatus()!= null ) switch (mediaPlayer.getStatus())
            {
                case PAUSED:
                    mediaPlayer.seek(mediaPlayer.getCurrentTime());
                    mediaPlayer.play();
                    break;
                case PLAYING:
                    mediaPlayer.pause();
                    break;
            }            
        }
    }
        
    public void loadMedia() // load button function
    {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open media File");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Mp3 Files", "*.mp4", "*.flv", "*.avi", "*.mp3"),
            new ExtensionFilter("All Files", "*.*")
        );

        stage = mv.getScene().getWindow(); // Get the window associated with the MediaView
        
        List<File> puffer = new ArrayList<>();
        puffer = fileChooser.showOpenMultipleDialog(stage);
        
        if(puffer != null && !puffer.isEmpty()){
            playlist.addAll(puffer);
        }
        
        playListVBox.getChildren().clear();        
        if(!playlist.isEmpty())
        {
            for(File file : playlist)
            {
                MediaFile mf = new MediaFile(file.getName(), playListVBox, file.toURI().toString(), index);
                mf.addEventHandler(DoubleClickEvent, event->
                {
                    playClickedMedia(file.toURI().toString());
                });
                index++;
            }
        }
        
    }
    
    
    public void loadDragNDropFiles()
    {
        if(!playlist.isEmpty())
        {
            System.out.println("loadDragNdropfiles list not empty");
            for(File file : playlist)
            {
                MediaFile mf = new MediaFile(file.getName(), playListVBox, file.toURI().toString(), index);
                mf.addEventHandler(DoubleClickEvent, event->
                {
                    playClickedMedia(file.toURI().toString());
                });
                index++;
            }
        } else {
            System.out.println("loadDragNdropfiles list is empty");
        }
    }
    
    public void clearPlayList()
    {
        selectedFile.delete();
        currentIndex = 0;
        index = 0;
        playListVBox.getChildren().clear();
        List<File> newPlaylist = new ArrayList<>(playlist);
        newPlaylist.clear();
        playlist = Collections.unmodifiableList(newPlaylist);
    }
    
    public void delSong()
    {
        System.out.println("del song");
    }

    private void playNextMedia() 
    {
        System.out.println("véget ért a szám");
        currentIndex++;
        if (currentIndex < playlist.size()) {
            playMedia();
        }
    }
    
   
    // zene lejátszása kattintásra
    public void playClickedMedia(String mediaSource)
    {
        
        media = new Media(mediaSource);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> playNextMedia());
        mv.setMediaPlayer(mediaPlayer);
        

        if (null != mediaPlayer.getStatus()) switch (mediaPlayer.getStatus())
        {
            case PAUSED:
                mediaPlayer.seek(mediaPlayer.getCurrentTime());
                mediaPlayer.play();
                break;
            case STOPPED:
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
                break;
            case UNKNOWN:
                mediaPlayer.play();
                break;
            default:
                break;
        }
        selectedFile = playlist.get(currentIndex);
    }
    
    public void stopMedia()
    {
        if (mediaPlayer != null &&  null != mediaPlayer.getStatus())
        {
            mediaPlayer.stop();
        }
        
    }
    
    public void nextMedia()
    {
        if (mediaPlayer != null &&  null != mediaPlayer.getStatus())
        {
            if(currentIndex + 1 < playlist.size()){
                mediaPlayer.stop();
                currentIndex++;
                System.out.println("currentIndex at nextMedia" + currentIndex);
                playMedia();
                
            }
        }
    }
    
    public void previousMedia()
    {
        System.out.println("RE");
        if (mediaPlayer != null &&  null != mediaPlayer.getStatus())
        {
            System.out.println("currentIndex at previousMedia " + currentIndex);
            if(currentIndex - 1 >= 0){
                mediaPlayer.stop();
                currentIndex--;
                System.out.println("currentIndex at previousMedia " + currentIndex);
                playMedia();
                
            }
        }
    }
    
 
    
    //hangerő szabályzás görgővel
    public void setScrollVolume()
    {
        if(mediaPlayer != null)
        {
            vol.setOnScroll((var e) -> 
            {
                double deltaY = e.getDeltaY();                
                double step = deltaY * 0.001;

                if(counter >= 0 && counter <= 1)
                {
                    counter += step;
                    if(counter < 0){
                        counter = 0;
                    } else if(counter > 1){
                        counter = 1;
                    }
                    
                    //-fx-background-color: linear-gradient(to top, rgb(110, 100, 200), transparent 10%);
                    int flooredCounter = (int) (Math.floor(counter*100));
                    String style = "";
                    if(flooredCounter == 100){
                        
                        style = "-fx-background-color: linear-gradient(to top, rgb(30,30,85), #8d99ae 100%, #8d99ae 100%);";
                    } else {
                        int first = 0;
                        if(flooredCounter < 100){
                            first = flooredCounter -10;
                        }                        
                        // -fx-background-color: linear-gradient(to top, rgb(30,30,85)), rgb(60,60,150) 60%);
                        style = "-fx-background-color: linear-gradient(to top, rgb(30,30,85), #8d99ae " + first + "%, transparent " + flooredCounter + "%);";
                    }                    
                    volMsg.setStyle(style);
                    
                    mediaPlayer.setVolume(counter);
                }
            });
        }
    }
    int asd = 0;
    
    public void volToScroll()
    {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));

        pauseTransition.setOnFinished(event -> 
        {
            v.setText("S");
            pauseTransition.setDuration(Duration.millis(100));
            pauseTransition.setOnFinished(event2 -> 
            {
                o.setText("C");                
                pauseTransition.setOnFinished(event3 -> 
                {
                    l.setText("R");
                    pauseTransition.setOnFinished(event4 -> 
                    {
                        u.setText("O");
                        pauseTransition.setOnFinished(event5 -> 
                        {
                            m.setText("L");
                            pauseTransition.setOnFinished(event6 -> 
                            {
                                e.setText("L");
                            });
                            pauseTransition.play();
                        });
                        pauseTransition.play();
                    });
                pauseTransition.play();
                });
                pauseTransition.play();
            });
            pauseTransition.play();
        });        
        
        pauseTransition.play();
    }
    public void scrollToVol()
    {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(100));

        pauseTransition.setOnFinished(event -> 
        {
            v.setText("V");
            pauseTransition.setDuration(Duration.millis(100));
            pauseTransition.setOnFinished(event2 -> 
            {
                o.setText("O");                
                pauseTransition.setOnFinished(event3 -> 
                {
                    l.setText("L");
                    pauseTransition.setOnFinished(event4 -> 
                    {
                        u.setText("U");
                        pauseTransition.setOnFinished(event5 -> 
                        {
                            m.setText("M");
                            pauseTransition.setOnFinished(event6 -> 
                            {
                                e.setText("E");
                            });
                            pauseTransition.play();
                        });
                        pauseTransition.play();
                    });
                pauseTransition.play();
                });
                pauseTransition.play();
            });
            pauseTransition.play();
        });        
        
        pauseTransition.play();
    }
    
}
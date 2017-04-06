package fileioproject;

import Views.TheFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Rebecca Broughton
 */



public class Controller {

  private final TheFrame frame = new TheFrame();
  private File openFile = null;
  String fileName = "  ";
  boolean fileModified = false;
  boolean saveOn = false;
  boolean saveAsOn = false;

  private static JFileChooser getFileChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

    chooser.addChoosableFileFilter(
        new FileNameExtensionFilter("Editable Files", "txt")
    );
    chooser.setAcceptAllFileFilterUsed(false);
    return chooser;
  }

  public Controller() {

    frame.setTitle(getClass().getSimpleName());
    frame.setLocationRelativeTo(null);
    setProperties(saveOn, saveAsOn, fileModified, fileName);
    
// event handlers

    frame.getOpenMenuItem().addActionListener(new ActionListener() {
      @Override

      public void actionPerformed(ActionEvent e) {
        
        if (fileModified) {
          int usersChoice = JOptionPane.showConfirmDialog(null, "OK to discard Changes?", "Choose", JOptionPane.YES_NO_CANCEL_OPTION);
          if (!(usersChoice == JOptionPane.YES_OPTION)) {
            return ;
          }
        }
        
        JFileChooser chooser = Controller.getFileChooser();
       
        int status = chooser.showOpenDialog(frame);
        
        if (status != JFileChooser.APPROVE_OPTION) {
          return;
        }
        
        openFile = chooser.getSelectedFile();
        Path path = openFile.toPath();

        try {
          String content = new String(Files.readAllBytes(path));
          frame.getTextArea().setText(content);
          frame.getTextArea().setEditable(true);
          frame.getTextArea().setCaretPosition(frame.getTextArea().getText().length());

          Path fileNamePath = path.getFileName();
          fileName = fileNamePath.toString();

          saveOn = true;
          saveAsOn = true;
          fileModified = false;
          setProperties(saveOn, saveAsOn, fileModified, fileName);
          frame.setTitle(fileName);

        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          JOptionPane.showMessageDialog(frame, "Cannot open file " + openFile);
        }
      }

    });

    frame.getNewMenuItem().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (fileModified) {
          int usersChoice = JOptionPane.showConfirmDialog(null, "OK to discard Changes?", "Choose", JOptionPane.YES_NO_CANCEL_OPTION);
          if (!(usersChoice == JOptionPane.YES_OPTION)) {
            return;
          }
        }
        frame.getTextArea().setEditable(true);
        frame.getTextArea().setText("");
        frame.getTextArea().setCaretPosition(0);
        fileName = "<NEW FILE>";
        frame.setTitle(fileName);

        saveOn = false;
        saveAsOn = true;
        fileModified = false;
        setProperties(saveOn, saveAsOn, fileModified, fileName);
      }
    });

    frame.getSaveMenuItem().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        try {
          Path outPath = Paths.get(fileName);
          //System.out.println(outPath);
          String content = frame.getTextArea().getText();
          Files.write(outPath, content.getBytes());

          saveOn = true;
          saveAsOn = true;
          fileModified = false;
          setProperties(saveOn, saveAsOn, fileModified, fileName);
          frame.setTitle(fileName);
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          JOptionPane.showMessageDialog(frame, "Cannot save file " + fileName);
        }
      }
    });

    frame.getSaveAsMenuItem().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        
        JFileChooser chooser = Controller.getFileChooser();
        int status = chooser.showSaveDialog(frame);
        if (status != JFileChooser.APPROVE_OPTION) {
          return;
        }

        File file = chooser.getSelectedFile();
        Path path = file.toPath();

        try {
          String pathName = path.toString().toLowerCase();
          String pattern = ".*\\.[a-z]+";

          if (!(pathName.matches(pattern))) {
            pathName += ".txt";
            file = new File(pathName);
            path = file.toPath();
          }

          if (file.exists()) {
            int usersChoice = JOptionPane.showConfirmDialog(null, "Overwrite Existing File?", "Choose", JOptionPane.YES_NO_CANCEL_OPTION);
            if (!(usersChoice == JOptionPane.YES_OPTION)) {
              return;
            }
          }

          Path fileNamePath = path.getFileName();
          fileName = fileNamePath.toString();

          String content = frame.getTextArea().getText();
          Files.write(fileNamePath, content.getBytes());

          saveOn = true;
          saveAsOn = true;
          fileModified = false;
          setProperties(saveOn, saveAsOn, fileModified, fileName);
          frame.setTitle(fileName);
          
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
          JOptionPane.showMessageDialog(frame, "Cannot save file " + file);
        }
      }
    });

    frame.getTextArea().addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        frame.getModTextField().setText(" * ");
        fileModified = true;
      }
    });

    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent evt) {
        if (fileModified) {
          int usersChoice = JOptionPane.showConfirmDialog(null, "OK to discard Changes?", "Choose", JOptionPane.YES_NO_CANCEL_OPTION);
          if (usersChoice == JOptionPane.YES_OPTION) {
            System.exit(0);
          }
        }
        else {
          System.exit(0);
        }
      }
    });

  }

  public static void main(String[] args) {
    Controller app = new Controller();
    app.frame.setVisible(true);
  }

  private void setProperties(boolean saveOn, boolean saveAsOn, boolean fileModified, String fileName) {
    frame.getSaveMenuItem().setEnabled(saveOn);
    frame.getSaveAsMenuItem().setEnabled(saveAsOn);
    frame.getTextField().setText(fileName);
    if (!fileModified) {
      frame.getModTextField().setText(" ");
    }
  }

}

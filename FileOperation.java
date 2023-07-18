import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;

public class FileOperation {

    // -- Attributes of the class
    Notepad notepad;

    boolean saved;
    boolean newFileFlag;
    String fileName;
    String applicationTitle = "Notepad - hemcodes";

    File file;
    JFileChooser fileChooser;

    // *******************************************

    // -- Constructor
// - Creates a new file chooser to allow the user to open and save files
    FileOperation(Notepad notepad) {
        this.notepad = notepad;

        saved = true;
        newFileFlag = true;
        fileName = "Untitled";
        file = new File(fileName);
        this.notepad.frame.setTitle(fileName + " - " + applicationTitle);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));

    } //end constructor
    //SaveFile method writes the contents of the notepad textarea
// to a file on the user's hard drive.
    boolean saveFile(File file)
    {
        FileWriter fileWriter=null;
        try
        {
            //Create a new file writer and use it to write the contents
            //of the text area to the file
            fileWriter=new FileWriter(file);
            fileWriter.write(notepad.textArea.getText());
        }
        catch(IOException ioe){
            updateStatus(file,false);
            return false;
        }
        finally
        {
            try{
                //Close the file writer
                fileWriter.close();
            }catch(IOException e){
                e.printStackTrace();
            } //end try catch
        } //end try catch finally

        //update the status of the file to saved
        updateStatus(file,true);
        return true;
    } //end saveFile method

    ////////////////////////
//Called when the user saves the file that is currently open.
    boolean saveThisFile()
    {

        if(!newFileFlag)
        {
            return saveFile(file);
        }//end if

        return saveAsFile();
    }
    ////////////////////////////////////
//Called when the user clicks on save as.
// Opens a dialog to allow them to save the current file
// as a new file.
    boolean saveAsFile()
    {
        File file1;
        fileChooser.setDialogTitle("Save As...");
        fileChooser.setApproveButtonText("Save Now");
        fileChooser.setApproveButtonMnemonic(KeyEvent.VK_S);
        fileChooser.setApproveButtonToolTipText("Click me to save!");

        //Loop infinitely to show the dialog until the user
        // saves the file
        do
        {
            //Show save dialog
            if(fileChooser.showSaveDialog(this.notepad.frame)!=JFileChooser.APPROVE_OPTION) {
                return false;
            }//end if

            //If the file was saved, stop the loop
            file1= fileChooser.getSelectedFile();
            if(!file1.exists()){
                break;
            }//end if

            //Show the confirm file save dialog
            if(JOptionPane.showConfirmDialog(
                    this.notepad.frame,"<html>"+file1.getPath()+" already exists.<br>Do you want to replace it?<html>",
                    "Save As",JOptionPane.YES_NO_OPTION
            )==JOptionPane.YES_OPTION) {
                break;
            }//end if

        }while(true); //end do while loop

        return saveFile(file1);
    } //end method

    ////////////////////////
// Reads in a file and displays it in the textarea of the notepad.
    boolean readFile(File file)
    {
        FileInputStream fileInputStream=null;
        BufferedReader bufferedReader=null;

        try
        {
            fileInputStream=new FileInputStream(file);
            bufferedReader=new BufferedReader(new InputStreamReader(fileInputStream));
            String currentLine=" ";

            //While there are lines in the file, read each line
            //and add it to the text area
            while(currentLine!=null)
            {
                currentLine=bufferedReader.readLine();
                if(currentLine==null)
                    break;
                this.notepad.textArea.append(currentLine+"\n");
            }//end while loop

        } catch(IOException ioe){
            updateStatus(file,false);
            return false;
        }
        finally
        {
            try{
                //Close the file input streams
                bufferedReader.close();
                fileInputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }//end try catch
        } //end try catch finally

        // -- Update the file status and set the cursor to the first character in the textarea
        updateStatus(file,true);
        this.notepad.textArea.setCaretPosition(0);
        return true;
    } //end method

    // -- Called when the user clicks on open file in the menu, displays a dialog to allow the user to choose the file

    void openFile(){
        if (!confirmSave()){
            return;
        } //end if

        // -- Set up the open file dialog
        fileChooser.setDialogTitle("Open File...");
        fileChooser.setApproveButtonText("Open this");
        fileChooser.setApproveButtonMnemonic(KeyEvent.VK_O);
        fileChooser.setApproveButtonToolTipText("Click me to open the selecteed file.!");

        File file1;

        // -- Keep looping to show the dialog until the user actually opens a file

        do {
            // -- show the dialog
            if (fileChooser.showOpenDialog(this.notepad.frame)!=JFileChooser.APPROVE_OPTION){
                return;
            } //end if

            // -- Get the file the user chose
            file1 = fileChooser.getSelectedFile();

            if (file1.exists()){
                break;
            } //end if

            // -- If the file couldn't be opened, then show a message to the user
            JOptionPane.showMessageDialog(this.notepad.frame, "<html>"+file1.getName()+"<br>file not found.<br>"+
                    "Please verify the correct file name was given.<html>", "Open", JOptionPane.INFORMATION_MESSAGE);
        } while (true); //end loop

        // -- Set the text area to blank
        this.notepad.textArea.setText("");

        // -- If the file can't be read, then it doesn't exist yet
        if (!readFile(file1)){
            // -- Set the filename to untitled until the user saves the file and names it
            fileName = "Untitled";
            saved=true;
            this.notepad.frame.setTitle(fileName+" - "+applicationTitle);
        } //end if

        // -- If the file can't be written to yet, set the new file flag to true
        if (!file1.canWrite()){
            newFileFlag = true;
        } //end if
    } //end method

    // **********************************

    // -- Update the file status while the file is saved
    void updateStatus(File file,boolean saved){
        if (saved){
            this.saved=true;
            fileName=file.getName();
            if (!file.canWrite()){
                // -- If you can't write to the file, set the file name to read only
                fileName+="(Read only)";
                newFileFlag=true;
            } //end if

            // -- Set the name of the file as the title in the frame and update the status bar
            this.file = file;
            notepad.frame.setTitle(fileName + " - " + applicationTitle);
            notepad.statusBar.setText("File : "+file.getPath()+" saved/opened successfully.");
            newFileFlag=false;
        } else {
            // -- Update the status bar
            notepad.statusBar.setText("Failed to save/open : "+file.getPath());
        } //end if
    } //end method

    // -- Shows an option pane dialof to prompt the user to confirm that they want to save the file
    boolean confirmSave(){
        String strMsg="<html>The text in the"+fileName+" file has been changed.<br>"+"Do you want to save the changes?<html>";

        if (!saved){
            int x=JOptionPane.showConfirmDialog(this.notepad.frame
            ,strMsg,applicationTitle,JOptionPane.YES_NO_CANCEL_OPTION);

            // -- Return false if the user hits the cancel or no buttons
            if (x == JOptionPane.CANCEL_OPTION){
                return false;
            } //end if

            if (x == JOptionPane.YES_OPTION && !saveAsFile()){
                return false;
            } //end if
        } //end if

        // -- Return true if the user clicked the yes button
        return true;
    } //end method

    // ***********************************************

    // -- Creates a new file
    void newFile(){
        if (!confirmSave()){
            return;
        } //end if

        // -- Reset the text area to blanl
        this.notepad.textArea.setText("");

        // -- Set the filename and title in the frame to untitled
        fileName="Untitled";
        file = new File(fileName);
        saved=true;
        newFileFlag=true;


    }































}
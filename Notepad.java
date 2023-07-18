import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class Notepad implements ActionListener {

    // -- Declare the attributes of the notepad class
    JFrame frame;
    JTextArea textArea;
    JLabel statusBar;

    private String fileName = "Untitled";

    String applicationName = "Notepad";

    FileOperation fileOperation;

    JColorChooser bcolorChooser = null;
    JColorChooser fcolorChooser = null;
    JDialog backgroundDialog = null;
    JDialog foregroundDialog = null;

     JMenuItem cutItem, copyItem, deleteItem, gotoItem, selectAllItem;

     final String fileText = "File";
     final String editText = "Edit";
     final String formatText = "Format";
     final String viewText = "View";
     final String helpText = "Help";

     final String fileNew = "New";
     final String fileOpen = "Open";
     final String fileSave = "Save";
     final String fileSaveAs = "Save As...";

     final String fileExit = "Exit";

     final String editUndo = "Undo";
     final String editCut = "Cut";
     final String editCopy = "Copy";
     final String editPaste = "Paste";
     final String editDelete = "Delete";

     final String editGoTo = "Go To...";
     final String editSelectAll = "Select All";
     final String editTimeDate = "Time/Date";

     final String formatWordWrap = "Word Wrap";
     final String formatForeground = "Set Text Color...";
     final String formatBackground = "Set Pad Color...";

     final String viewStatusBar = "Status Bar";

     final String helpHelpTopic = "Help Topic";
     final String helpAboutNotepad = "About Notepad";

     final String aboutText = "This notepad application was developed for Summit Academy OIC";

     // --- Main Method
     public static void main(String[] args) {

         // -- Create a new Notepad
         // -> Since the constructor contains all the code to create the GUI, all we need to do in the main method is call the constructor
         new Notepad();

     } //end main

    // ***** CONSTRUCTOR
    // -> Creates and shows the GUI
    // **********************************************

    Notepad() {

        // -- Create a new frame, textarea, and a label for the status bar
        frame = new JFrame(fileName + " - " + applicationName);
        textArea = new JTextArea(30, 60);
        statusBar = new JLabel("|| Ln 1, Col 1 ", JLabel.RIGHT);

        // -- Add a scroll pane for the text editor and add the status bar to the frame
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);
        frame.add(new JLabel(" "), BorderLayout.EAST);
        frame.add(new JLabel(" "), BorderLayout.WEST);

        // -- Create a new menu for the notepad editor
        createMenuBar(frame);

        // -- Set the window to the preferred size, set the location and set it to be visible
        frame.pack();
        frame.setLocation(100, 50);
        frame.setVisible(true);

        // -- Set the frame to do nothing when the user clicks the X to close the window
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // -- Create a new file operation to handle opening, closing, and saving files
        fileOperation = new FileOperation(this);

        // -- Add a caret listener to "listen" for changes to the next text area
        // - this listener will respond to the the user moving the cursor
        textArea.addCaretListener(
                new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        int lineNumber = 0, column = 0, pos = 0;

                        try {
                            pos = textArea.getCaretPosition();
                            lineNumber = textArea.getLineOfOffset(pos);
                            column = pos - textArea.getLineStartOffset(lineNumber);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        } //end catch

                        if (textArea.getText().length() == 0) {
                            lineNumber = 0;
                            column = 0;
                        } //end if

                        // -- When the user moves the cursor, update the line and column numbers on the status bar
                        statusBar.setText("||   Ln" + (lineNumber + 1) + ", Col " + (column + 1));
                    } // end caretUpdate method
                }); // end Listener

        // -- Add a listerner to "listen" for the changes the user makes to their document
        // - to know if they need to be prompted to save the document
        DocumentListener myListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fileOperation.saved = false;
            } //end method


            @Override
            public void removeUpdate(DocumentEvent e) {
                fileOperation.saved = false;
            } //end method

            @Override
            public void changedUpdate(DocumentEvent e) {
                fileOperation.saved = false;
            } //end method

        }; //end document listener

        // -- Add the document listener to the text area in the notepad app
        textArea.getDocument().addDocumentListener(myListener);

        // -- Add a window listener to handle when the user closes the app
        WindowListener frameClose = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                // - Prompt the user to save their file
                if (fileOperation.confirmSave()) {
                    System.exit(0);
                } //end if
            } //end method

        }; // end listener

        // -- Add the listener to the window
        frame.addWindowListener(frameClose);

    } //end constructor method

    // --  The go-to method puts the cursor at the line number specified by the user
    void goTo(){
         int linenumber = 0;

         try{
             // -> Get the current position of the cusor
             linenumber = textArea.getLineOfOffset(textArea.getCaretPosition())+1;

             // -> Prompt the user to enter the line number
             String tempStr = JOptionPane.showInputDialog(frame, "Enter Line Number:", ""+linenumber);

             // -> If the user didn't enter a line number, then end the method
             if (tempStr==null){
                 return;
             } //end if

             // -- Get the line number the user entered and set the cursor position to the line number
             linenumber = Integer.parseInt(tempStr);
             textArea.setCaretPosition(textArea.getLineStartOffset(linenumber-1));
         } catch (Exception e){
             e.printStackTrace();
         } //end try catch

    } //end go-to method

    // -- This method performs an action based on what the user chose on the menu.
    // - for example, when the user clicks on new file, the method creates a new file

    public void actionPerformed(ActionEvent ev){

         // -> get the menu item the user clicked
        String cmdText = ev.getActionCommand();

        // -> Based on the menu item the user clicked, perform the corresponding action
        switch (cmdText){
            case fileNew:
                fileOperation.newFile();
                break;

            case fileOpen:
                fileOperation.openFile();
                break;
            case fileSave:
                fileOperation.saveThisFile();
                break;
            case fileSaveAs:
                fileOperation.saveAsFile();
                break;
            case fileExit:
                if (fileOperation.confirmSave()) System.exit(0);
                break;

            case editCut:
                textArea.cut();
                break;
            case editCopy:
                textArea.copy();
                break;
            case editPaste:
                textArea.paste();
                break;
            case editDelete:
                textArea.replaceSelection("");
                break;
            case editGoTo:
                if (Notepad.this.textArea.getText().length() == 0)
                    return; // text box have no text
                goTo();
                break;

            case editSelectAll:
                textArea.selectAll();
                break;

            case editTimeDate:
                textArea.insert(new Date().toString(), textArea.getSelectionStart());
                break;

            case formatWordWrap: {

                // -- Update the check box next to word wrap on the menu
                JCheckBoxMenuItem temp = (JCheckBoxMenuItem) ev.getSource();
                textArea.setLineWrap(temp.isSelected());
                break;
            } //end formatWordWrap

            case formatForeground:
                showForegroundColorDialog();
                break;

            case formatBackground:
                showBackgroundColorDialog();
                break;

            case viewStatusBar: {
                JCheckBoxMenuItem temp = (JCheckBoxMenuItem) ev.getSource();
                statusBar.setVisible(temp.isSelected());
                break;
            } //end viewStatusBar

            case helpAboutNotepad:
                JOptionPane.showMessageDialog(Notepad.this.frame, aboutText, "About Notepad",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

        } //end switch


    } //end method action


    // -- This method allows the user to change the background of the textarea in the notepad app
    // - the method shows a dialog box with a color picker
    void showBackgroundColorDialog() {
        // -- If the background color chooser is null, create a new color chooser
        if (bcolorChooser == null) {
            bcolorChooser = new JColorChooser();
        } //end if

        // -- If the dialog is null, create a new dialog and add the color chooser to it
        if (backgroundDialog == null)
            backgroundDialog = JColorChooser.createDialog(Notepad.this.frame,
                    formatBackground,
                    false,
                    bcolorChooser,
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Notepad.this.textArea.setBackground(bcolorChooser.getColor());
                        } //end action
                    }, //end action listener
                    null);

        // -- Show the dialog
        backgroundDialog.setVisible(true);
    } //end method

    // -- This method allows the user to change the color of the text in the notepad app
    // - The method shows a dialog with a color picker
    void showForegroundColorDialog(){
         if(fcolorChooser==null){
             fcolorChooser=new JColorChooser();
         } //end if

        if (foregroundDialog==null){
            foregroundDialog = JColorChooser.createDialog(Notepad.this.frame, formatForeground, false, fcolorChooser, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Notepad.this.textArea.setForeground(fcolorChooser.getColor());
                } //end method
            }, //end listener
                    null);
        } //end if


        // -- Show the dialog
        foregroundDialog.setVisible(true);


    } //end method



    // -- The createMenuItem methods add a new item to the drop-down menu in the notepad app
    // and add a listener to respond if the user clicks on the menu item

    JMenuItem createMenuItem(String s, int key, JMenu toMenu, ActionListener al){

         // -> Create a new menu item
         JMenuItem menuItem = new JMenuItem(s, key);

         // -> Add an action listener to the menu item to "listen" for clicks on the menu item
         menuItem.addActionListener(al);

         // -> Add the menu item to the menu
         toMenu.add(menuItem);

         return menuItem;
    } //end method

    // **********************************

    JMenuItem createMenuItem(String s, int key, JMenu toMenu, int aclKey, ActionListener al){
         JMenuItem menuItem = new JMenuItem(s, key);
         menuItem.addActionListener(al);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK));
         toMenu.add(menuItem);

         return menuItem;
    } //end method


    // **********************************

    // -- The createMenuItem methods add a new checkbox item to the dropdown menu
    // in the notepad app and add a listener to respond if the user clicks the checkbox
    JCheckBoxMenuItem createCheckBoxMenuItem(String s,
                                             int key, JMenu toMenu, ActionListener al){
         JCheckBoxMenuItem temp = new JCheckBoxMenuItem(s);
         temp.setMnemonic(key);
         temp.addActionListener(al);
         temp.setSelected(false);
         toMenu.add(temp);

         return temp;


    } //end method

    // -- Creates a new drop down menu for the app, for example, file, edit, view menus
    JMenu createMenu(String s, int key, JMenuBar toMenuBar){
         JMenu menu = new JMenu(s);
         menu.setMnemonic(key);
         toMenuBar.add(menu);
         return menu;
    }

// -- The createMenuBar method adds a menu bar in the notepad app
    void createMenuBar(JFrame f){

         JMenuBar menuBar = new JMenuBar();
         JMenuItem menuItem;

         JMenu fileMenu = createMenu(fileText, KeyEvent.VK_F, menuBar);
         JMenu editMenu = createMenu(editText, KeyEvent.VK_E, menuBar);
         JMenu formatMenu = createMenu(formatText, KeyEvent.VK_O, menuBar);
         JMenu viewMenu = createMenu(viewText, KeyEvent.VK_V, menuBar);
         JMenu helpMenu = createMenu(helpText, KeyEvent.VK_H, menuBar);

         createMenuItem(fileNew, KeyEvent.VK_N, fileMenu, KeyEvent.VK_N, this);
         createMenuItem(fileOpen, KeyEvent.VK_O, fileMenu, KeyEvent.VK_O, this);
         createMenuItem(fileSave, KeyEvent.VK_S, fileMenu, KeyEvent.VK_S, this);
         createMenuItem(fileSaveAs, KeyEvent.VK_A, fileMenu, KeyEvent.VK_A, this);
         fileMenu.addSeparator();
         createMenuItem(fileExit, KeyEvent.VK_X, fileMenu, this);

         menuItem = createMenuItem(editUndo, KeyEvent.VK_U,editMenu, KeyEvent.VK_Z, this);
         menuItem.setEnabled(false);
         editMenu.addSeparator();
         cutItem = createMenuItem(editCut, KeyEvent.VK_T, editMenu, KeyEvent.VK_X, this);
         copyItem = createMenuItem(editCopy,KeyEvent.VK_C, editMenu, KeyEvent.VK_C, this);
         createMenuItem(editPaste, KeyEvent.VK_P, editMenu, KeyEvent.VK_V, this);
         deleteItem = createMenuItem(editDelete, KeyEvent.VK_L, editMenu, this);
         deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
         editMenu.addSeparator();

         gotoItem = createMenuItem(editGoTo, KeyEvent.VK_G, editMenu, KeyEvent.VK_G, this);
         editMenu.addSeparator();
         selectAllItem = createMenuItem(editSelectAll, KeyEvent.VK_A, editMenu, KeyEvent.VK_A, this);
         createMenuItem(editTimeDate, KeyEvent.VK_D, editMenu, this).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

         createCheckBoxMenuItem(formatForeground, KeyEvent.VK_W,formatMenu, this);

         formatMenu.addSeparator();
         createMenuItem(formatForeground, KeyEvent.VK_T,formatMenu,this);
         createMenuItem(formatBackground, KeyEvent.VK_P, formatMenu, this);

         createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this).setSelected(true);

         menuItem = createMenuItem(helpHelpTopic, KeyEvent.VK_H, helpMenu, this);
         menuItem.setEnabled(false);
         helpMenu.addSeparator();
         createMenuItem(helpAboutNotepad, KeyEvent.VK_A, helpMenu, this);

         // -- Add a listener to response when the user clicks on one of the drop down menus in the menu bar

        MenuListener editMenuListener = new MenuListener() {
            public void menuSelected(MenuEvent menuEvent) {
                if (Notepad.this.textArea.getText().length()==0)
                {
                    selectAllItem.setEnabled(false);
                    gotoItem.setEnabled(false);
                } else {
                    selectAllItem.setEnabled(true);
                    gotoItem.setEnabled(true);
                } //end if

                if (Notepad.this.textArea.getSelectionStart()== textArea.getSelectionEnd())
                {
                    cutItem.setEnabled(false);
                    copyItem.setEnabled(false);
                    deleteItem.setEnabled(false);
                } else {
                    cutItem.setEnabled(true);
                    copyItem.setEnabled(true);
                    deleteItem.setEnabled(true);

                } //end if

            } //end selected method

            public void menuDeselected(MenuEvent menuEvent) {

            } //end menu deselected method

            public void menuCanceled(MenuEvent menuEvent) {

            } //end menu canceled method
        }; //end listener

        // -- Add the listener to the menu
        editMenu.addMenuListener(editMenuListener);

        // -- Add the menu bar to the frame/window
        f.setJMenuBar(menuBar);
     } //end menu listener



}//end class

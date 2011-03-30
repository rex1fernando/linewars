package menu.panels;

import java.awt.Color;
import java.awt.Graphics;

import menu.WindowManager;
import menu.components.MenuButton;

public class TitlePanel extends javax.swing.JPanel {

	private static final long serialVersionUID = -8190374307173604577L;
	private WindowManager windowManager;
	
    public TitlePanel(WindowManager windowManager) {
    	this.windowManager = windowManager;
        initComponents();
    }
                      
    private void initComponents() {

        createGameButton = new MenuButton();
        joinGameButton = new MenuButton();
        launchEditorButton = new MenuButton();
        optionsButton = new MenuButton();
        creditsButton = new MenuButton();
        exitButton = new MenuButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(100, 200, 100, 200));
        setMaximumSize(new java.awt.Dimension(1024, 640));
        setMinimumSize(new java.awt.Dimension(1024, 640));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(1024, 640));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridLayout(6, 1, 0, 25));

        createGameButton.setText("Create Game");
        createGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createGameButtonActionPerformed(evt);
            }
        });
        add(createGameButton);

        joinGameButton.setText("Join Game");
        joinGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinGameButtonActionPerformed(evt);
            }
        });
        add(joinGameButton);

        launchEditorButton.setText("Launch Editor");
        launchEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                launchEditorButtonActionPerformed(evt);
            }
        });
        add(launchEditorButton);

        optionsButton.setText("Options");
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsButtonActionPerformed(evt);
            }
        });
        add(optionsButton);

        creditsButton.setText("Credits");
        creditsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                creditsButtonActionPerformed(evt);
            }
        });
        add(creditsButton);

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        add(exitButton);
        
        setOpaque(false);
    }                  

    private void createGameButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        windowManager.gotoCreateGame();
    }                                                

    private void joinGameButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               
        windowManager.gotoJoinGame();
    }                                              

    private void launchEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                   
    	windowManager.gotoEditor();
    }                                                  

    private void optionsButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              
    	windowManager.gotoOptions();
    }                                             

    private void creditsButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              
        windowManager.gotoCredits();
    }                                             

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {                                     
    	windowManager.exitGame();
    }
          
    private javax.swing.JButton createGameButton;
    private javax.swing.JButton creditsButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton joinGameButton;
    private javax.swing.JButton launchEditorButton;
    private javax.swing.JButton optionsButton;
}


package menu.creategame;

import java.awt.Color;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import menu.WindowManager;
import menu.networking.PlayerBean;

public class CreateGamePanel extends javax.swing.JPanel {

    /** Creates new form CreateGamePanel */
    public CreateGamePanel(WindowManager wm) {
        initComponents();
    }
    
    public void startServer() {
    	// TODO implement
    }
    
    public void startClient(String serverIp) {
    	// TODO implement
    }

    public void setReplay(boolean isReplay) {
    	// TODO implement
    }
    
    public void setSelection(Object selection) {
    	// TODO implement
    }
    
    public void setPlayerName(int playerId, String name) {
    	// TODO implement
    }
    
    public void setPlayerSlot(int playerId, int slot) {
    	// TODO implement
    }
    
    public void setPlayerRace(int playerId, Object race) {
    	// TODO implement
    }
    
    public void setPlayerColor(int playerId, Color color) {
    	// TODO implement
    }
    
    public void updateChat(String message) {
    	// TODO implement
    }
    
    public void updatePlayerPanel(int playerId, PlayerBean info) {
    	// TODO implement
    }
    
    public void addPlayer(boolean enabled) {
    	// TODO implement
    }
    
    public void removePlayer(int playerId) {
    	// TODO implement
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jComboBox2 = new javax.swing.JComboBox();
        replayLabel = new javax.swing.JLabel();
        selectionLabel = new javax.swing.JLabel();
        replayToggleButton = new javax.swing.JToggleButton();
        selectionComboBox = new javax.swing.JComboBox();
        previewPanel = new javax.swing.JPanel();
        chatWindow = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextArea();
        chatField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        lobbyScrollPane = new javax.swing.JScrollPane();
        lobbyPanel = new javax.swing.JPanel();

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setMaximumSize(new java.awt.Dimension(1024, 640));
        setMinimumSize(new java.awt.Dimension(1024, 640));

        replayLabel.setFont(new java.awt.Font("Ubuntu", 1, 18));
        replayLabel.setText("Replay");
        replayLabel.setRequestFocusEnabled(false);

        selectionLabel.setFont(new java.awt.Font("Ubuntu", 1, 18));
        selectionLabel.setText("Map Selection");
        selectionLabel.setRequestFocusEnabled(false);

        replayToggleButton.setFocusPainted(false);
        replayToggleButton.setFocusable(false);
        replayToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replayCheckBoxActionPerformed(evt);
            }
        });

        selectionComboBox.setModel(new SelectionComboBoxModel());
        selectionComboBox.setFocusable(false);
        selectionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectionBoxActionPerformed(evt);
            }
        });

        previewPanel.setBackground(new java.awt.Color(254, 254, 254));
        previewPanel.setPreferredSize(new java.awt.Dimension(250, 250));

        javax.swing.GroupLayout previewPanelLayout = new javax.swing.GroupLayout(previewPanel);
        previewPanel.setLayout(previewPanelLayout);
        previewPanelLayout.setHorizontalGroup(
            previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        previewPanelLayout.setVerticalGroup(
            previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        chatWindow.setMaximumSize(new java.awt.Dimension(250, 250));
        chatWindow.setMinimumSize(new java.awt.Dimension(250, 250));
        chatWindow.setPreferredSize(new java.awt.Dimension(250, 250));

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        chatArea.setColumns(20);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setRows(5);
        chatArea.setBorder(null);
        chatArea.setFocusable(false);
        jScrollPane1.setViewportView(chatArea);

        sendButton.setText("Send");
        sendButton.setFocusable(false);
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout chatWindowLayout = new javax.swing.GroupLayout(chatWindow);
        chatWindow.setLayout(chatWindowLayout);
        chatWindowLayout.setHorizontalGroup(
            chatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chatWindowLayout.createSequentialGroup()
                .addComponent(chatField, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendButton))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );
        chatWindowLayout.setVerticalGroup(
            chatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chatWindowLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(chatWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton)))
        );

        buttonPanel.setMaximumSize(new java.awt.Dimension(100, 25));
        buttonPanel.setMinimumSize(new java.awt.Dimension(100, 25));
        buttonPanel.setPreferredSize(new java.awt.Dimension(100, 25));
        buttonPanel.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        startButton.setText("Start");
        startButton.setFocusable(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(startButton);

        cancelButton.setText("Cancel");
        cancelButton.setFocusable(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        lobbyScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        lobbyScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        lobbyPanel.setLayout(new javax.swing.BoxLayout(lobbyPanel, javax.swing.BoxLayout.Y_AXIS));

        lobbyScrollPane.setViewportView(lobbyPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(replayLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(replayToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(selectionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectionComboBox, 0, 461, Short.MAX_VALUE))
                    .addComponent(lobbyScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(previewPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chatWindow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(previewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chatWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(replayToggleButton, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(replayLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(selectionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(selectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lobbyScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>                        

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void selectionBoxActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void replayCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {                                               
        javax.swing.JToggleButton btn = (javax.swing.JToggleButton) evt.getSource();
        if (btn.isSelected()) {
            selectionLabel.setText("Replay Selection");
            // TODO populate replays
            // TODO disable color, slot, and race of all players
        } else {
            selectionLabel.setText("Map Selection");
            // TODO populate maps
            // TODO enable color, slot, and race of all players
        }
    }                                              

    private String[] getAvailableMaps() {
        return new String[]{"Map 1", "Map 2", "Map 3"};
    }

    private String[] getAvailableReplays() {
        return new String[]{"Replay 1", "Replay 2"};
    }

    private String[] getAvailableRaces() {
        return new String[]{"Race 1", "Race 2", "Race 3", "Race 4"};
    }
    
    private Color[] getAvailableColors() {
    	return new Color[] { Color.black, Color.white, Color.red, Color.blue, Color.green };
    }

    private class SelectionComboBoxModel implements ComboBoxModel {
        private int map = 0;
        private int replay = 0;

        public void setSelectedItem(Object anItem) {
            String[] str =  getItems();
            for (int i = 0; i < str.length; ++i) {
                if (str[i].equals(anItem)) {
                    if (replayToggleButton.isSelected()) replay = i; else map = i;
                }
            }
        }

        public Object getSelectedItem() {
            return getElementAt(replayToggleButton.isSelected() ? replay : map);
        }

        public int getSize() {
            return getItems().length;
        }

        public Object getElementAt(int index) {
            return getItems()[index];
        }

        public void addListDataListener(ListDataListener l) {}
        public void removeListDataListener(ListDataListener l) {}

        private String[] getItems() {
            return (replayToggleButton.isSelected())
                    ? getAvailableReplays() : getAvailableMaps();
        }
    }

    // Variables declaration - do not modify                     
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextArea chatArea;
    private javax.swing.JTextField chatField;
    private javax.swing.JPanel chatWindow;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel lobbyPanel;
    private javax.swing.JScrollPane lobbyScrollPane;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JLabel replayLabel;
    private javax.swing.JToggleButton replayToggleButton;
    private javax.swing.JComboBox selectionComboBox;
    private javax.swing.JLabel selectionLabel;
    private javax.swing.JButton sendButton;
    private javax.swing.JButton startButton;
    // End of variables declaration      
    

    public class PlayerPanel extends javax.swing.JPanel {

        /** Creates new form playerPanel */
        public PlayerPanel() {
            initComponents();
        }

        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
        private void initComponents() {

            slot = new javax.swing.JComboBox();
            race = new javax.swing.JComboBox();
            name = new javax.swing.JButton();
            color = new javax.swing.JComboBox();

            setMaximumSize(new java.awt.Dimension(712, 28));
            setMinimumSize(new java.awt.Dimension(712, 28));
            setPreferredSize(new java.awt.Dimension(712, 28));

            slot.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }));
            slot.setBorder(null);
            slot.setEditor(null);
            slot.setFocusable(false);
            slot.setKeySelectionManager(null);
            slot.setMaximumSize(new java.awt.Dimension(82, 28));
            slot.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    slotChangeActionPerformed(evt);
                }
            });

            race.setModel(new javax.swing.DefaultComboBoxModel(getAvailableRaces()));
            race.setBorder(null);
            race.setFocusable(false);
            race.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    raceChangeActionPerformed(evt);
                }
            });

            name.setText("Name");
            name.setFocusable(false);
            name.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            name.setMaximumSize(new java.awt.Dimension(53, 28));
            name.setMinimumSize(new java.awt.Dimension(53, 28));
            name.setPreferredSize(new java.awt.Dimension(53, 28));
            name.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    nameButtonActionPerformed(evt);
                }
            });

            color.setModel(new javax.swing.DefaultComboBoxModel(getAvailableColors()));
            color.setBorder(null);
            color.setEditor(null);
            color.setFocusable(false);
            color.setKeySelectionManager(null);
            color.setMaximumSize(new java.awt.Dimension(82, 28));
            color.setRenderer(new ComboBoxRenderer());
            color.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    colorChangeActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(color, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(slot, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(race, 0, 359, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(slot, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(color, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(race, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
            );
        }// </editor-fold>                        

        private void raceChangeActionPerformed(java.awt.event.ActionEvent evt) {                                           
            // TODO add your handling code here:
        }                                          

        private void nameButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
            String s = (String) javax.swing.JOptionPane.showInputDialog(this,
                    "Please enter a new name:",
                    "Rename",
                    javax.swing.JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    name.getText());
            
            if (s != null && !s.equals(name.getText())) {
                name.setText(s);
                // TODO send stuff over the network
            }
        }                                          

        private void slotChangeActionPerformed(java.awt.event.ActionEvent evt) {                                           
            // TODO add your handling code here:
    }                                          

        private void colorChangeActionPerformed(java.awt.event.ActionEvent evt) {                                            
            // TODO add your handling code here:
        }

        // Variables declaration - do not modify                     
        private javax.swing.JComboBox color;
        private javax.swing.JButton name;
        private javax.swing.JComboBox race;
        private javax.swing.JComboBox slot;
        // End of variables declaration                   

    }


}

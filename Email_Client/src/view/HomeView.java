/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import model.DataRequest;
import model.EmailMessage;
import model.User;

/**
 *
 * @author tom18
 */
public class HomeView extends javax.swing.JFrame {

    private static User user;
    private static Socket socket;

    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    private String[] columnNames
            = {
                "Subject",
                "From",
                "Content"
            };
    private DefaultTableModel tableModel1 = null;
    private DefaultTableModel tableModel2 = null;
    private DefaultTableModel tableModel3 = null;

    /**
     * Creates new form HomeView
     */
    public HomeView(User user, Socket socket, ObjectOutputStream oos, ObjectInputStream ois) throws IOException, ClassNotFoundException {
        this.user = user;
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
        this.setTitle("Trang chủ");
        HomeView.socket = socket;

        this.oos = oos;
        this.ois = ois;

        setTableSize(jTable2);
        setTableSize(jTable3);
        tableModel1 = (DefaultTableModel) jTable2.getModel();
        tableModel2 = (DefaultTableModel) jTable3.getModel();
        tableModel3 = (DefaultTableModel) jTable1.getModel();

        List<EmailMessage> messageList = new ArrayList<>();
        messageList = this.getMailList(user, "getMessage", 0);
        addData(messageList, tableModel1);

        ChangeListener changeListener;
        changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                DataRequest dataRequest = new DataRequest();
                dataRequest.setData(user);
                List<EmailMessage> newEmailMessage = new ArrayList<>();
                if (jTabbedPane1.getSelectedIndex() == 0) {
                    tableModel1.setRowCount(0);
                    dataRequest.setMethodName("getMessage");
                    dataRequest.setType(0);
                    newEmailMessage = getData(dataRequest);
                    addData(newEmailMessage, tableModel1);
                } else if (jTabbedPane1.getSelectedIndex() == 1) {
                    tableModel2.setRowCount(0);
                    dataRequest.setMethodName("getMessage");
                    dataRequest.setType(1);
                    newEmailMessage = getData(dataRequest);
                    addData(newEmailMessage, tableModel2);
                    detectTableRowClick(jTable3, newEmailMessage);
                } else if (jTabbedPane1.getSelectedIndex() == 2) {
                    tableModel3.setRowCount(0);
                    dataRequest.setMethodName("getMessage");
                    dataRequest.setType(2);
                    newEmailMessage = getData(dataRequest);
                    addData(newEmailMessage, tableModel3);
                    detectTableRowClick(jTable1, newEmailMessage);
                }
            }

            private List<EmailMessage> getData(DataRequest dataRequest) {
                try {
                    oos.writeObject(dataRequest);
                    oos.flush();

                    List<EmailMessage> list = (List<EmailMessage>) ois.readObject();
                    System.out.println(list.size());
                    return list;
                } catch (IOException ex) {
                    Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        jTabbedPane1.addChangeListener(changeListener);
        detectTableRowClick(jTable2, messageList);
        closeConnection();
    }

    private void setTableSize(JTable jtable) {
        jtable.getColumnModel().getColumn(0).setMaxWidth(300);
        jtable.getColumnModel().getColumn(1).setMaxWidth(1000);
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    private void addData(List<EmailMessage> messageList, DefaultTableModel tableModel) {
        for (EmailMessage em : messageList) {
            tableModel.addRow((em.toObject()));
        }
    }

    private List<EmailMessage> getMailList(User user, String methodName, Integer type) throws IOException, ClassNotFoundException {
        DataRequest dataRequest = new DataRequest();
        dataRequest.setUser(user);
        dataRequest.setMethodName(methodName);
        dataRequest.setType(type);
        oos.writeObject(dataRequest);
        oos.flush();

        List<EmailMessage> messageList = (List<EmailMessage>) ois.readObject();
        return messageList;
    }

    private void detectTableRowClick(JTable jTable, List<EmailMessage> messageList) {
        jTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    try {
                        // to detect doble click events
                        int row = jTable.getSelectedRow(); // select a row

                        DataRequest dataRequest = new DataRequest();
                        dataRequest.setMethodName("getDetailEmail");
                        dataRequest.setUser(user);
                        dataRequest.setData(messageList.get(row));

                        oos.writeObject(dataRequest);
                        oos.flush();
                        List<EmailMessage> messageList = (List<EmailMessage>) ois.readObject();
                        for (EmailMessage message : messageList) {
                            System.out.println(message);
                        }

//                        DetailView detailView = new DetailView(user, socket, messageList.get(row), oos, ois);
//                        detailView.setVisible(true);
//                        setVisible(false);
                    } catch (IOException ex) {
                        Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    private void closeConnection() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    if (oos != null) {
                        oos.flush();
                        oos.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LoginView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jTabbedPane1.addTab("Hộp thư đến", jScrollPane2);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable3);

        jTabbedPane1.addTab("Có gắn dấu sao", jScrollPane4);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("Quan trọng", jScrollPane1);

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Subject"
            }
        ));
        jScrollPane5.setViewportView(jTable4);

        jTabbedPane1.addTab("Thùng rác", jScrollPane5);

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable5);

        jTabbedPane1.addTab("Thư nháp", jScrollPane6);

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTable6);

        jTabbedPane1.addTab("Thư rác", jScrollPane7);

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTable7);

        jTabbedPane1.addTab("Thư đã gửi", jScrollPane8);

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(jTable8);

        jTabbedPane1.addTab("Tất cả thư", jScrollPane9);

        jButton1.setText("Gửi mail");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        SendEmailView sendEmailView = new SendEmailView(user, socket, oos, ois);
        sendEmailView.setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new HomeView(user, socket, oos, ois).setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    // End of variables declaration//GEN-END:variables
}

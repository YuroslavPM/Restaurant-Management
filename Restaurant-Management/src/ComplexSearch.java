import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComplexSearch extends JPanel {
    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result;
    int id=-1;
    JPanel upPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JPanel downPanel = new JPanel();
    JLabel fNameL = new JLabel("Име:");
    JLabel lNameL = new JLabel("Фамилия:");
    JLabel cityL = new JLabel("Град:");


    JTextField fNameTF = new JTextField();
    JTextField lNameTF = new JTextField();
    JTextField cityTF = new JTextField();


    JComboBox<String> restaurantCombo = new JComboBox<String>();
    //Mid secction components

    JButton searchBt = new JButton("Търсене");


    //Down panel components
    JTable table = new JTable();
    JScrollPane myScroll = new JScrollPane(table);

    public ComplexSearch(){
        this.setSize(400,600);
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setLayout(new GridLayout(3,1));

        //First panel



        upPanel.setLayout(new GridLayout(7,2));
        upPanel.add(fNameL);
        upPanel.add(fNameTF);
        upPanel.add(lNameL);
        upPanel.add(lNameTF);
        upPanel.add(cityL);
        upPanel.add(cityTF);

        //this.add(upPanel);

        //Mid panel


        midPanel.add(searchBt);

        //midPanel.add(restaurantCombo);
        //this.add(midPanel);


        searchBt.addActionListener(new ComplexSearch.SearchAction());

        //Down panel
        myScroll.setPreferredSize(new Dimension(350,350));
        downPanel.add(myScroll);
        // this.add(downPanel);

        table.addMouseListener(new ComplexSearch.MouseAction());
        //refreshTable();

        this.add(upPanel);
        this.add(midPanel);
        this.add(downPanel);

        //Run panel
        this.setVisible(true);
    }

    public void clearForm(){
        lNameTF.setText("");
        cityTF.setText("");
        fNameTF.setText("");
    }


/*
    public void refreshTable(){
        conn = DbConnection.getConnection();
        try {
            state= conn.prepareStatement("Select p.fname, p.lname, r.city, s.reservationdate from PERSONS p join SERVICES s on s.PERSONID = p.PERSONID join RESTAURANTS r on s.RESTAURANTID = r.RESTAURANTID where p.fname = ? and p.lname = ? and r.city = ?");
            result = state.executeQuery();
            table.setModel(new MyModel(result));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
*/


    class MouseAction implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row=table.getSelectedRow();
            id = Integer.parseInt(table.getValueAt(row,0).toString());
            fNameTF.setText(table.getValueAt(row,1).toString());
            lNameTF.setText(table.getValueAt(row,2).toString());
            cityTF.setText(table.getValueAt(row,3).toString());


        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }


    class SearchAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            Connection conn = DbConnection.getConnection();
            String sql = "Select p.fname, p.lname, r.city, s.reservationdate from PERSONS p join SERVICES s on s.PERSONID = p.PERSONID join RESTAURANTS r on s.RESTAURANTID = r.RESTAURANTID where p.fname = ? and p.lname = ? and r.city = ?";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1,fNameTF.getText());
                state.setString(2,lNameTF.getText());
                state.setString(3,cityTF.getText());

                result = state.executeQuery();
                try {
                    table.setModel(new MyModel(result));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class RefreshAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //refreshTable();
            clearForm();
        }
    }
}

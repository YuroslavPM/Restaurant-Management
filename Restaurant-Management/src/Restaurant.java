import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;

public class Restaurant extends JPanel {

    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result;
    int id=-1;
    JPanel upPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JPanel downPanel = new JPanel();
    JLabel adressL = new JLabel("Адрес:");
    JLabel cityL = new JLabel("Град:");
    JLabel ratingL = new JLabel("Рейтинг:");
    JLabel capacityL = new JLabel("Капацитет:");

    JTextField adressTF = new JTextField();
    JTextField cityTF = new JTextField();
    JTextField ratingTF = new JTextField();
    JTextField capacityTF = new JTextField();



    JComboBox<String> restaurantCombo = new JComboBox<String>();
    //Mid secction components
    JButton addBt = new JButton("Добавяне");
    JButton deleteBt = new JButton("Изтриване");
    JButton editBt = new JButton("Редактиране");

    JButton searchBt = new JButton("Търсене по град");

    JButton refreshBt = new JButton("Обнови");

    //Down panel components
    JTable table = new JTable();
    JScrollPane myScroll = new JScrollPane(table);

    public Restaurant(){
        this.setSize(400,600);
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setLayout(new GridLayout(3,1));

        //First panel



        upPanel.setLayout(new GridLayout(7,2));
        upPanel.add(adressL);
        upPanel.add(adressTF);
        upPanel.add(cityL);
        upPanel.add(cityTF);
        upPanel.add(ratingL);
        upPanel.add(ratingTF);
        upPanel.add(capacityL);
        upPanel.add(capacityTF);

        //this.add(upPanel);

        //Mid panel

        midPanel.add(addBt);
        midPanel.add(editBt);
        midPanel.add(deleteBt);
        midPanel.add(searchBt);
        midPanel.add(refreshBt);
        //midPanel.add(restaurantCombo);
        //this.add(midPanel);

        addBt.addActionListener(new Restaurant.AddAction());
        deleteBt.addActionListener(new Restaurant.DeleteAction());
        editBt.addActionListener(new Restaurant.EditAction());
        searchBt.addActionListener(new Restaurant.SearchAction());
        refreshBt.addActionListener(new Restaurant.RefreshAction());
        //Down panel
        myScroll.setPreferredSize(new Dimension(350,350));
        downPanel.add(myScroll);
        // this.add(downPanel);

        table.addMouseListener(new Restaurant.MouseAction());
        refreshTable();
        refreshComboBox();

        this.add(upPanel);
        this.add(midPanel);
        this.add(downPanel);

        //Run panel
        this.setVisible(true);
    }

    public void clearForm(){
        adressTF.setText("");
        cityTF.setText("");
        ratingTF.setText("");
        capacityTF.setText("");
    }


    public void refreshComboBox(){
        conn = DbConnection.getConnection();
        String sql="select restaurantid,address,city,rating,capacity from Restaurants";
        String item="";


        try {
            state=conn.prepareStatement(sql);
            result = state.executeQuery();

            while(result.next()){
                item = result.getObject(1).toString()+". "+result.getObject(2).toString()+" "+
                        result.getObject(3).toString();
                restaurantCombo.addItem(item);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshTable(){
        conn = DbConnection.getConnection();
        try {
            state= conn.prepareStatement("select * from restaurants");
            result = state.executeQuery();
            table.setModel(new MyModel(result));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    class AddAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DbConnection.getConnection();
            String sql="insert into RESTAURANTS(address,city,rating,capacity) values(?,?,?,?)";
            try {
                state= conn.prepareStatement(sql);
                state.setString(1,adressTF.getText());
                state.setString(2,cityTF.getText());
                state.setInt(3,Integer.parseInt(ratingTF.getText()));
                state.setInt(4,Integer.parseInt(capacityTF.getText()));

                state.execute();
                refreshTable();
                refreshComboBox();
                clearForm();


            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class MouseAction implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row=table.getSelectedRow();
            id = Integer.parseInt(table.getValueAt(row,0).toString());
            adressTF.setText(table.getValueAt(row,1).toString());
            cityTF.setText(table.getValueAt(row,2).toString());
            ratingTF.setText(table.getValueAt(row,3).toString());
            capacityTF.setText(table.getValueAt(row,4).toString());


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

    class EditAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            Connection conn = DbConnection.getConnection();
                String sql = "update restaurants set address=?, city=?, rating=?, capacity=? where restaurantid=?";

            try {
                state = conn.prepareStatement(sql);
                state.setString(1, adressTF.getText());
                state.setString(2, cityTF.getText());
                state.setInt(3, Integer.parseInt(ratingTF.getText()));
                state.setInt(4, Integer.parseInt(capacityTF.getText()));
                state.setInt(5,id);
                state.execute();
                refreshTable();
                refreshComboBox();
                clearForm();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    class DeleteAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            Connection conn = DbConnection.getConnection();
            String sql="delete from restaurants where restaurantid=?";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1,id);
                state.execute();
                refreshTable();
                refreshComboBox();
                clearForm();
                id=-1;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class SearchAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            Connection conn = DbConnection.getConnection();
            String sql = "select * from restaurants where city=?";
            try {
                state = conn.prepareStatement(sql);
                state.setString(1,cityTF.getText());
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
            refreshTable();
            clearForm();
        }
    }


}

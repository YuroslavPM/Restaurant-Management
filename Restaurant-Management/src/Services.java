import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Services extends JPanel {

    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result;
    int id=-1;
    JPanel upPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JPanel downPanel = new JPanel();
    JLabel clientL = new JLabel("Клиент:");
    JLabel restaurantL = new JLabel("Ресторант:");

    JLabel reserveDateL = new JLabel("Дата:");

    JTextField reserveDateTF = new JTextField();

    JComboBox<String> personCombo = new JComboBox<String>();
    private Set<String> itemsSetPersonCombo = new HashSet<>();;
    JComboBox<String> restaurantCombo = new JComboBox<String>();
    private Set<String> itemsSetRestaurantCombo = new HashSet<>();;

    //Mid secction components
    JButton addBt = new JButton("Добавяне");
    JButton deleteBt = new JButton("Изтриване");
    JButton editBt = new JButton("Редактиране");

    JButton searchBt = new JButton("Търсене по дата");

    JButton refreshBt = new JButton("Обнови");

    //Down panel components
    JTable table = new JTable();
    JScrollPane myScroll = new JScrollPane(table);
    int targetPersonID=0;
    String fName="";
    String egn="";
    int targetRestaurantID=0;
    String city ="";
    String address="";
    public Services(){
        this.setSize(400,600);
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setLayout(new GridLayout(3,1));

        //First panel



        upPanel.setLayout(new GridLayout(7,2));
        upPanel.add(clientL);
        upPanel.add(personCombo);
        upPanel.add(restaurantL);
        upPanel.add(restaurantCombo);
        upPanel.add(reserveDateL);
        upPanel.add(reserveDateTF);


        //this.add(upPanel);

        //Mid panel

        midPanel.add(addBt);
        midPanel.add(editBt);
        midPanel.add(deleteBt);
        midPanel.add(searchBt);
        midPanel.add(refreshBt);

        //this.add(midPanel);

        addBt.addActionListener(new Services.AddAction());
        deleteBt.addActionListener(new Services.DeleteAction());
        searchBt.addActionListener(new Services.SearchAction());
        editBt.addActionListener(new Services.EditAction());
        refreshBt.addActionListener(new Services.RefreshAction());
        //Down panel
        myScroll.setPreferredSize(new Dimension(350,350));
        downPanel.add(myScroll);
        // this.add(downPanel);

        table.addMouseListener(new Services.MouseAction());
        refreshTable();
        refreshPersonBox();
        refreshComboBox();

        this.add(upPanel);
        this.add(midPanel);
        this.add(downPanel);

        //Run panel
        this.setVisible(true);
    }

    public void clearForm(){

        reserveDateTF.setText("");

    }
    public void refreshPersonBox(){
        conn = DbConnection.getConnection();
        String sql="select PersonID,FName,Egn from Persons";
        String item="";


        try {
            state=conn.prepareStatement(sql);
            result = state.executeQuery();

            while(result.next()){
                item = result.getObject(2).toString()+" с ЕГН: "+result.getObject(3).toString();
                if (!itemsSetPersonCombo.contains(item)) { // Verificar si el elemento ya existe en el conjunto
                    personCombo.addItem(item);
                    itemsSetPersonCombo.add(item); // Agregar el elemento al conjunto
                }


            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void refreshComboBox(){
        conn = DbConnection.getConnection();
        String sql="select restaurantID,address,city from Restaurants";
        String item="";


        try {
            state=conn.prepareStatement(sql);
            result = state.executeQuery();

            while(result.next()){
                item = result.getObject(1).toString()+". Град:"+result.getObject(3).toString()+" с адрес: "+result.getObject(2).toString();

                if (!itemsSetRestaurantCombo.contains(item)) { // Verificar si el elemento ya existe en el conjunto
                    restaurantCombo.addItem(item);
                    itemsSetRestaurantCombo.add(item); // Agregar el elemento al conjunto
                }
                targetRestaurantID = ((int) result.getObject(1));
                address = result.getObject(2).toString();
                city = result.getObject(3).toString();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshTable(){
        conn = DbConnection.getConnection();
        try {
            state= conn.prepareStatement("select * from Services");
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

            String sql1="SELECT personID, FName FROM persons WHERE egn = ?";
            String sql2="SELECT restaurantId,address,city FROM restaurants WHERE restaurantId = ?";
            String sql3="insert into Services(PersonID,RestaurantID,FName,Egn,Address,City,ReservationDate) values(?,?,?,?,?,?,?)";

            try {
                state= conn.prepareStatement(sql1);
                //searching egn
                String selectedPerson = personCombo.getSelectedItem().toString();

                String egn = selectedPerson.split(": ")[1];
                //Get Person data
                state.setString(1,egn);
                result = state.executeQuery();
                int personID=-1;
                String fName="";
                while (result.next()) {
                    personID = result.getInt("personID");
                    fName = result.getString("FName");

                }
                //searching restaurantID

                state = conn.prepareStatement(sql2);
                Integer restaurantId = Integer.parseInt(restaurantCombo.getSelectedItem().toString().substring(0,restaurantCombo.getSelectedItem().toString().indexOf(".")));
                state.setInt(1, restaurantId);
                result = state.executeQuery();

                String selectedRestaurant = restaurantCombo.getSelectedItem().toString();
                String Address="";
                String City ="";
                while (result.next()) {
                    Address=result.getString("address");
                    City = result.getString("city");

                }





                state= conn.prepareStatement(sql3);
                //put data
                state.setInt(1,personID);
                state.setInt(2,restaurantId);
                state.setString(3,fName);
                state.setString(4,egn);
                state.setString(5,Address);
                state.setString(6,City);
                String date = reserveDateTF.getText();
                state.setDate(7, Date.valueOf(reserveDateTF.getText()));


                state.execute();
                refreshTable();
                refreshPersonBox();
                refreshComboBox();
                clearForm();


            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class EditAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            Connection conn = DbConnection.getConnection();
            String sql = "update services set reservationdate=? where orderID=?";

            try {
                state = conn.prepareStatement(sql);
                state.setDate(1,Date.valueOf(reserveDateTF.getText()));
                state.setInt(2,id);
                state.execute();
                refreshTable();
                refreshPersonBox();
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

            reserveDateTF.setText(table.getValueAt(row,7).toString());



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

    class DeleteAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            Connection conn = DbConnection.getConnection();
            String sql="delete from services where OrderID=?";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1,id);
                state.execute();
                refreshTable();
                refreshPersonBox();
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
            String sql = "select * from services where reservationdate=?";
            try {
                state = conn.prepareStatement(sql);
                state.setDate(1,Date.valueOf(reserveDateTF.getText()));
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


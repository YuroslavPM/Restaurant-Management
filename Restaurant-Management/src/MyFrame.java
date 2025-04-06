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

public class MyFrame extends JFrame {

    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result;
    int id=-1;
    JPanel upPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JPanel downPanel = new JPanel();
    JTabbedPane tab = new JTabbedPane();
    JPanel clientPanel = new JPanel();
    JPanel restaurantPanel = new Restaurant();
    JPanel servicesPanel = new Services();
    JPanel complexSearchPanel = new ComplexSearch();
    JLabel fNameL = new JLabel("Име:");
    JLabel lNameL = new JLabel("Фамилия:");
    JLabel genderL = new JLabel("Пол:");
    JLabel ageL = new JLabel("Възраст:");
    JLabel egnL = new JLabel("ЕГН:");

    JTextField fNameTF = new JTextField();
    JTextField lNameTF = new JTextField();
    JTextField ageTF = new JTextField();
    JTextField egnTF = new JTextField();

    String[] item = {"Mъж","Жена"};
    JComboBox<String> genderCombo = new JComboBox<String>(item);
    JComboBox<String> personCombo = new JComboBox<String>();

    //Mid secction components
    JButton addBt = new JButton("Добавяне");
    JButton deleteBt = new JButton("Изтриване");
    JButton editBt = new JButton("Редактиране");

    JButton searchBt = new JButton("Търсене по години");

    JButton refreshBt = new JButton("Обнови");

    //Down panel components
    JTable table = new JTable();
    JScrollPane myScroll = new JScrollPane(table);




    public MyFrame(){
        this.setSize(400,600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setLayout(new GridLayout(3,1));

        //First panel
        tab.add(clientPanel,"Клиенти");
        tab.add(restaurantPanel,"Ресторант");
        tab.add(servicesPanel,"Резервация");
        tab.add(complexSearchPanel,"Справка");
        clientPanel.setLayout(new GridLayout(3,1));
        restaurantPanel.setLayout(new GridLayout(3,1));
        servicesPanel.setLayout(new GridLayout(3,1));

        upPanel.setLayout(new GridLayout(7,2));
        upPanel.add(fNameL);
        upPanel.add(fNameTF);
        upPanel.add(lNameL);
        upPanel.add(lNameTF);
        upPanel.add(genderL);
        upPanel.add(genderCombo);
        upPanel.add(ageL);
        upPanel.add(ageTF);
        upPanel.add(egnL);
        upPanel.add(egnTF);
        clientPanel.add(upPanel);
        //this.add(upPanel);

        //Mid panel

        midPanel.add(addBt);
        midPanel.add(editBt);
        midPanel.add(deleteBt);
        midPanel.add(searchBt);
        midPanel.add(refreshBt);
        //midPanel.add(personCombo);
        clientPanel.add(midPanel);
        //this.add(midPanel);

        addBt.addActionListener(new AddAction());
        deleteBt.addActionListener(new DeleteAction());
        editBt.addActionListener(new EditAction());
        searchBt.addActionListener(new SearchAction());
        refreshBt.addActionListener(new RefreshAction());
        //Down panel
        myScroll.setPreferredSize(new Dimension(350,350));
        downPanel.add(myScroll);
        clientPanel.add(downPanel);
       // this.add(downPanel);
        this.add(tab);
        table.addMouseListener(new MouseAction());

        refreshTable();
        refreshComboBox();
        //Run panel
        this.setVisible(true);
    }

    public void clearForm(){
        fNameTF.setText("");
        lNameTF.setText("");
        ageTF.setText("");
        egnTF.setText("");
    }

    public void refreshComboBox(){
        conn = DbConnection.getConnection();
        String sql="select personid,fname,lname,gender,age,egn from Persons";
        String item="";


        try {
            state=conn.prepareStatement(sql);
            result = state.executeQuery();

            while(result.next()){
                item = result.getObject(1).toString()+". "+result.getObject(2).toString()+" "+
                        result.getObject(3).toString();
                personCombo.addItem(item);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshTable(){
        conn = DbConnection.getConnection();
        try {
            state= conn.prepareStatement("select * from persons");
            result = state.executeQuery();
            table.setModel(new MyModel(result));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    class AddAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            conn = DbConnection.getConnection();
            String sql="insert into PERSONS(fname,lname,gender,age,egn) values(?,?,?,?,?)";
            try {
                state= conn.prepareStatement(sql);
                state.setString(1,fNameTF.getText());
                state.setString(2,lNameTF.getText());
                state.setString(3,genderCombo.getSelectedItem().toString());
                state.setInt(4,Integer.parseInt(ageTF.getText()));
                state.setString(5, egnTF.getText());

                state.execute();
                refreshTable();
                refreshComboBox();
                clearForm();


            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class MouseAction implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            int row=table.getSelectedRow();
            id = Integer.parseInt(table.getValueAt(row,0).toString());
            fNameTF.setText(table.getValueAt(row,1).toString());
            lNameTF.setText(table.getValueAt(row,2).toString());

            ageTF.setText(table.getValueAt(row,4).toString());
            egnTF.setText(table.getValueAt(row,5).toString());

            if (table.getValueAt(row,3).toString().equals("Мъж")){
                genderCombo.setSelectedIndex(0);
            }
            else{
                genderCombo.setSelectedIndex(1);
            }

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
            String sql = "update persons set fname=?, lname=?, gender=?, age=?,egn=? where personid=?";

            try {
                state = conn.prepareStatement(sql);
                state.setString(1, fNameTF.getText());
                state.setString(2, lNameTF.getText());
                state.setString(3,genderCombo.getSelectedItem().toString());
                state.setInt(4, Integer.parseInt(ageTF.getText()));
                state.setString(5, egnTF.getText());
                state.setInt(6,id);
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
            String sql="delete from persons where id=?";
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
            String sql = "select * from persons where age=?";
            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, Integer.parseInt(ageTF.getText()));
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

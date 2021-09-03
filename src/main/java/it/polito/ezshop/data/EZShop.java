package it.polito.ezshop.data;

import java.sql.*;

import it.polito.ezshop.exceptions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.*;

import static it.polito.ezshop.data.MyProductType.validateProductCode;

public class EZShop implements EZShopInterface{
    private static Connection conn;
    private User loggedUser;

    private List<ProductType> inventory = new ArrayList<>();
    private List<Customer> customerList = new ArrayList<>();
    //private List<BalanceOperation> balanceOperationList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Order> orderList = new ArrayList<>();
    private boolean isOrderListUpdated = false;
    //private boolean isBalanceOperationUpdated = false;
    private boolean isCustomerListUpdated = false;
    private boolean isUserListUpdated = false;
    private boolean isInventoryUpdated = false;

    public EZShop()  {
        // open db connection
        try {
            // db parameters
            String url = "jdbc:sqlite:ezshop_db.sqlite";
            // create a connection to the database
            if(conn==null)
            {
                conn = DriverManager.getConnection(url);
                System.out.println("Connection to SQLite has been established.");
            }

        } catch (SQLException e) {
            System.out.println("Database connection fail. Aborting...");
            System.exit(-1);
        }
    }

    @Override
    public void reset() {
        this.isOrderListUpdated = false;
        //this.isBalanceOperationUpdated = false;
        this.isCustomerListUpdated = false;
        this.isUserListUpdated = false;
        this.isInventoryUpdated = false;

        // logout current user
        this.loggedUser = null;
        
        try {
            // empty all tables
            String sql = "DELETE FROM balanceOperation WHERE true";
            PreparedStatement st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM saleTransaction WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM returnTransaction WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM 'order' WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM productType WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM productEntry WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM user WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM customer WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM loyaltyCard WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM creditCard WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            sql = "DELETE FROM product WHERE true";
            st = conn.prepareStatement(sql);
            st.executeUpdate();

            sql = "UPDATE sqlite_sequence SET seq=0 WHERE name!='returnTransaction'";
            st = conn.prepareStatement(sql);
            st.executeUpdate();

            sql = "UPDATE sqlite_sequence SET seq=1 WHERE name='returnTransaction'";
            st = conn.prepareStatement(sql);
            st.executeUpdate();

            // commit&close connection
            conn.commit();
            conn.close();

        } catch (SQLException ignored) {
            
        }
    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        // username not null, not empty
        if(username == null || username.equals(""))
            throw new InvalidUsernameException("Invalid Username");

        // password not null, not empty
        if(password == null || password.equals("")){
            throw new InvalidPasswordException("Invalid Password");
        }

        // role not null, not empty, not Administrator&&ShopManager&&Cashier
        if(role == null || role.isEmpty() || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){
            throw new InvalidRoleException("Invalid Role");
        }

        // insert the new user
        try {
            String sql = "INSERT INTO user(username, password, role) VALUES (?, ?, ?)";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, password);
            st.setString(3, role);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0){
                return -1;
            }
            isUserListUpdated = false;
            return st.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            return -1;
        }
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        // check role of the user (only administrator)
        if(loggedUser == null || !loggedUser.getRole().equals("Administrator")) {
            throw new UnauthorizedException("Unauthorized");
        }

        // id not null, not <= 0
        if(id == null || id <= 0) {
            throw new InvalidUserIdException("Invalid User id");
        }

        // delete user using id
        try {
            String sql="DELETE FROM user WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            int deletedRows = st.executeUpdate();
            //conn.commit();

            if(deletedRows == 0)
                return false;

            isUserListUpdated = false;
            //st.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        // check role of the user (only administrator)
        if (loggedUser==null || !loggedUser.getRole().equals("Administrator")) {
            throw new UnauthorizedException();
        }

        // if cached userList is not updated, download from db
        if(!isUserListUpdated) {
            List<User> list = new ArrayList<>();
            try {
                String sql = "SELECT id, password, role, username FROM user";
                PreparedStatement st = conn.prepareStatement(sql);
                ResultSet rs = st.executeQuery();

                while (rs.next()) {
                    list.add(new MyUser(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    ));
                }
                userList = list;
                isUserListUpdated = true;
                return userList;
            } catch (SQLException e) {
                // list empty if there are problems with db
                return list;
            }
        }
        else
            return userList;
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        // check role of the user (only administrator)
        if (loggedUser == null || !loggedUser.getRole().equals("Administrator")) {
            throw new UnauthorizedException();
        }

        // id not null, not <= 0
        if (id == null || id <= 0) {
            throw new InvalidUserIdException();
        }

        User user;
        try {
            String sql = "SELECT id, password, role, username FROM user WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                // no product with the given code
                return null;

            user = new MyUser(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
            );
            return user;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        // check role of the user (only administrator)
        if(loggedUser == null || !loggedUser.getRole().equals("Administrator")) {
            throw new UnauthorizedException();
        }

        // id not null, not <= 0
        if (id == null || id <= 0) {
            throw new InvalidUserIdException();
        }

        // role is Administrator||ShopManager||Cashier
        if (role == null || !role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier")) {
            throw new InvalidRoleException();
        }

        try {
            String sql = "UPDATE user SET role=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, role);
            st.setInt(2, id);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                return false;

            isUserListUpdated = false;
            return true;
        } catch (SQLException e) {

            return false;
        }
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        // there is already a logged user
        if(loggedUser != null)
            return null;
        // username not null, not empty
        if(username == null || username.isEmpty())
            throw new InvalidUsernameException();
        // password not null, not empty
        if(password == null || password.isEmpty())
            throw new InvalidPasswordException();

        try {
            String sql = "SELECT id, password, role, username FROM user WHERE username=? AND password=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                return null;

            User user = new MyUser(rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
            );

            loggedUser = user;
            return user;

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean logout() {
        if (loggedUser != null) {
            loggedUser = null;
            return true;
        }
        else
            return false;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        // check role of the user (only administrator and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // productCode not null, not empty
        if(productCode == null || productCode.equals(""))
            throw new InvalidProductCodeException("Invalid Product Code");

        // check if productCode is valid
        if(!validateProductCode(productCode)) {
            throw new InvalidProductCodeException();
        }

        // description not null, not empty
        if(description == null || description.equals("")){
            throw new InvalidProductDescriptionException("Invalid Product Description");
        }

        // pricePerUnit not <= 0
        if(pricePerUnit <= 0){
            throw new InvalidPricePerUnitException("Invalid Price Per unit");
        }

        // if note is null an empty string should be saved
        if(note == null)
            note = "";

        // insert the new productType
        try {
            String sql="INSERT INTO productType(productCode, description, pricePerUnit, quantity, notes) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, productCode);
            st.setString(2, description);
            st.setDouble(3, pricePerUnit);
            st.setInt(4, 0);
            st.setString(5, note);

            int updatedRows = st.executeUpdate();
            //conn.commit();

            if(updatedRows == 0)
                return -1;
            
            // get Id generated in the db from row inserted
            isInventoryUpdated = false;
            return st.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            // product already present or db problem
            return -1;
        }
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        // check role of the user (only administrator and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
           throw new UnauthorizedException();

        // check id of the product (not <=0)
        if(id == null || id <= 0)
            throw new InvalidProductIdException("Invalid Product Id");

        // description not null, not empty
        if(newDescription == null || newDescription.equals("")){
            throw new InvalidProductDescriptionException();
        }

        // pricePerUnit not <=0
        if(newPrice <= 0){
            throw new InvalidPricePerUnitException();
        }

        // barCode not null, not empty
        if(newCode == null || newCode.equals(""))
            throw  new InvalidProductCodeException();

        // if note is null an empty string should be saved
        if(newNote == null)
            newNote = "";

        // check if newCode is valid
        if(!validateProductCode(newCode)) {
            throw new InvalidProductCodeException();
        }

        try {
            String sql = "UPDATE productType SET productCode=?, description=?, pricePerUnit=?, notes=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, newCode);
            st.setString(2, newDescription);
            st.setDouble(3, newPrice);
            st.setString(4, newNote);
            st.setInt(5, id);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                // no product with the given id
                return false;

            isInventoryUpdated = false;
            return true;
        } catch (SQLException e) {
            // another product already has the new barcode provided or db problem
            return false;
        }
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        // check role of the user (only administrator and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // check id of the product (not <=0)
        if(id == null || id <= 0)
            throw new InvalidProductIdException();

        try {
            String sql="DELETE FROM ProductType WHERE id=?" ;
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,id);
            int deletedRows = st.executeUpdate();

            if(deletedRows == 0)
                // no product deleted
                return false;

            isInventoryUpdated = false;
            return true;
        } catch (SQLException e) {
            // db problem
            return false;
        }
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager") && (!loggedUser.getRole().equals("Cashier")))))
            throw new UnauthorizedException();

        if(!isInventoryUpdated) {
            List<ProductType> list = new ArrayList<>();
            try {
                String sql = "SELECT id, productCode, description, pricePerUnit, quantity, notes, position FROM productType";
                PreparedStatement st = conn.prepareStatement(sql);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    list.add(new MyProductType(
                            rs.getInt("id"),
                            rs.getString("productCode"),
                            rs.getString("description"),
                            rs.getDouble("pricePerUnit"),
                            rs.getInt("quantity"),
                            rs.getString("notes"),
                            rs.getString("position")
                    ));
                }
                inventory = list;
                isInventoryUpdated = true;
                return inventory;
            } catch (SQLException e) {
                // db problem
                return list;
            }
        }
        else
            return inventory;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // barCode not null, not empty
        if(barCode == null || barCode.equals(""))
            throw  new InvalidProductCodeException();

        // check if barCode is valid
        if(!validateProductCode(barCode)) {
            throw new InvalidProductCodeException();
        }

        ProductType product;
        try {
            String sql = "SELECT id, productCode, description, pricePerUnit, quantity, notes, position FROM productType WHERE productCode=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, barCode);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                // no product with the given code
                return null;

            product = new MyProductType(
                    rs.getInt("id"),
                    rs.getString("productCode"),
                    rs.getString("description"),
                    rs.getDouble("pricePerUnit"),
                    rs.getInt("quantity"),
                    rs.getString("notes"),
                    rs.getString("position")
            );
            return product;
        } catch (SQLException e) {
            // problems with db connection
            return null;
        }
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // null should be considered as the empty string
        if(description == null)
           description = "";
        
        List<ProductType> list = new ArrayList<>();
        try {
            String sql = "SELECT id, productCode, description, pricePerUnit, quantity, notes, position FROM ProductType WHERE description LIKE ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, '%' + description + '%');
            ResultSet rs = st.executeQuery();

            while (rs.next()){
                list.add(new MyProductType(
                        rs.getInt("id"),
                        rs.getString("productCode"),
                        rs.getString("description"),
                        rs.getDouble("pricePerUnit"),
                        rs.getInt("quantity"),
                        rs.getString("notes"),
                        rs.getString("position")
                        )
                );
            }

            return list;
        } catch (SQLException e) {
            // problems with db connection
            return list;
        }
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // check id of the product (not <=0)
        if(productId == null || productId <= 0)
            throw new InvalidProductIdException();

        try {
            String sql="UPDATE productType SET quantity=quantity+? WHERE id=? AND position IS NOT NULL";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,toBeAdded);
            st.setInt(2,productId);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                // quantity would be negative or productType has not an assigned location
                return false;

            isInventoryUpdated = false;
            return true;
        } catch (SQLException e) {
            // db problem
            return false;
        }
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        // if newPos is null, position should be empty
        if(newPos == null){
            newPos = "";
        }

        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // check id of the product (not <=0)
        if(productId == null || productId <= 0)
            throw new InvalidProductIdException();

        // check position format (number-string-number)
        if(!newPos.equals("") && !newPos.matches("[0-9]+-[a-zA-Z]+-[0-9]+")){
            throw new InvalidLocationException("Invalid Location");
        }

        try {
            String sql="UPDATE productType SET position=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1,newPos);
            st.setInt(2,productId);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                // productId not exist
                return false;

            isInventoryUpdated = false;
            return true;
        } catch (SQLException e) {
            // db problem or position not unique
            return false;
        }
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        //check quantity is not <=0
        if(quantity <= 0)
            throw new InvalidQuantityException("Invalid Quantity");

        //check pricePerUnit is not <=0
        if(pricePerUnit <= 0)
            throw new InvalidPricePerUnitException();

        //check if the product exist and if barcode is valid
        ProductType product = this.getProductTypeByBarCode(productCode);
        if(product == null)
            return -1;

        // insert the new productType
        try {
            String sql = "INSERT INTO 'order'(productCode, pricePerUnit, quantity, status) VALUES (?, ?, ?, ?)";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, productCode);
            st.setDouble(2, pricePerUnit);
            st.setInt(3, quantity);
            st.setString(4,"ISSUED");
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                // cannot update order
                return -1;

            isOrderListUpdated = false;
            // new id of the order created
            return st.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            // db problem
            return -1;
        }
    }

    // issue order + pay order
    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        // check role of the user (only administrator and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        //check quantity is not <=0
        if(quantity <= 0)
            throw new InvalidQuantityException();

        //check pricePerUnit is not <=0
        if(pricePerUnit <= 0)
            throw new InvalidPricePerUnitException();

        //check if the product exist
        ProductType product = this.getProductTypeByBarCode(productCode);
        if(product == null)
            return -1;

        // check balance and THEN record payed order if enough money
        if(recordBalanceUpdate(-pricePerUnit*quantity)) {
            try {
                String sql = "INSERT INTO 'order'(productCode, pricePerUnit, quantity, status) VALUES (?, ?, ?, ?)";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setString(1, productCode);
                st.setDouble(2, pricePerUnit);
                st.setInt(3, quantity);
                st.setString(4, "PAYED");
                int updatedRows = st.executeUpdate();

                if (updatedRows == 0)
                    return -1;

                // record the order on the balance
                isOrderListUpdated = false;
                return st.getGeneratedKeys().getInt(1);
            } catch (SQLException e) {
                return -1;
            }
        }
        else
            return -1;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // orderId not null, not <=0
        if(orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException("Invalid order Id");
        }

        //String actualStatus;
        double toBeAdded;
        try {
            String sql = "SELECT quantity, pricePerUnit FROM 'order' WHERE id=? AND status='ISSUED'" ;
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, orderId);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                // no orderId found
                return false;

            //actualStatus = rs.getString("status");
            toBeAdded = -rs.getDouble("pricePerUnit") * rs.getInt("quantity");
        } catch (SQLException e) {
            return false;
        }

//        if(actualStatus.equals("PAYED"))
//            // order not in ISSUED state
//            return false;

        // check balance and THEN record payed order if enough money
        //String oldRole=loggedUser.getRole();
        //loggedUser.setRole("Administrator");
        if(recordBalanceUpdate(toBeAdded)) {
            // change status to PAYED
            try {
                String sql = "UPDATE 'order' SET status=? WHERE id=?";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setString(1, "PAYED");
                st.setInt(2, orderId);
                int updatedRows = st.executeUpdate();

                if (updatedRows == 0)
                    return false;

                isOrderListUpdated = false;
                //loggedUser.setRole(oldRole);
                return true;
            } catch (SQLException e) {
                //loggedUser.setRole(oldRole);
                return false;
            }
        }
        else {
            //loggedUser.setRole(oldRole);
            return false;
        }
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // orderId not null, not <=0
        if(orderId == null || orderId <= 0){
            throw new InvalidOrderIdException();
        }

        int quantity;
        String productCode;
        ProductType product;
        //String actualStatus;
        try {
            String sql = "SELECT quantity, productCode FROM 'order' WHERE id=? AND status='PAYED'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, orderId);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                // no orderId found
                return false;

            quantity = rs.getInt("quantity");
            productCode = rs.getString("productCode");
            //actualStatus = rs.getString("status");
            product = this.getProductTypeByBarCode(productCode);
        } catch (Exception e) {
            return false;
        }

        if(product.getLocation() == null || product.getLocation().equals("")){
            throw new InvalidLocationException();
        }
//        if(actualStatus.equals("COMPLETED"))
//            return false;

        // update product quantity
        try {
            this.updateQuantity(product.getId(), quantity);
            isInventoryUpdated = false;
        } catch(Exception e){
            return false;
        }

        // set order status to COMPLETED
        try {
            String sql="UPDATE 'order' SET status=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1,"COMPLETED");
            st.setInt(2,orderId);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                return false;

            isOrderListUpdated = false;
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException, InvalidRFIDException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && (!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        // orderId not null, not <=0
        if(orderId == null || orderId <= 0){
            throw new InvalidOrderIdException();
        }

        if(RFIDfrom== null || RFIDfrom.length()!=12 || !RFIDfrom.matches("^[0-9]{12}$"))
        {
            throw new InvalidRFIDException();
        }

        int quantity;
        String productCode;
        ProductType product;
        //String actualStatus;
        try {
            String sql = "SELECT quantity, productCode FROM 'order' WHERE id=? AND status='PAYED'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, orderId);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                // no orderId found
                return false;

            quantity = rs.getInt("quantity");
            productCode = rs.getString("productCode");

            //actualStatus = rs.getString("status");
            product = this.getProductTypeByBarCode(productCode);
        } catch (Exception e) {
            return false;
        }

        String sql2;
        for(int i=0;i<quantity;i++){
            try {
                sql2 = "SELECT * FROM product WHERE RFID=?";
                PreparedStatement st2 = conn.prepareStatement(sql2);
                st2.setString(1, String.format("%1$12d", Integer.parseInt(RFIDfrom) + i).replace(' ', '0'));

                ResultSet rs2 = st2.executeQuery();
                if(rs2.isBeforeFirst()){
                    throw new InvalidRFIDException();
                }

            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }

        if(product.getLocation() == null || product.getLocation().equals("")){
            throw new InvalidLocationException();
        }

        try {
            this.updateQuantity(product.getId(), quantity);
            isInventoryUpdated = false;
        } catch(Exception e){
            return false;
        }

        // set order status to COMPLETED
        try {
            String sql="UPDATE 'order' SET status=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1,"COMPLETED");
            st.setInt(2,orderId);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                return false;

            isOrderListUpdated = false;

        } catch (SQLException e) {
            return false;
        }

        //RFID UPDATE
        String sql3;
        for(int i=0;i<quantity;i++){
            try {
                sql3 = "INSERT INTO product(RFID,barcode) VALUES (?,?)";
                PreparedStatement st3 = conn.prepareStatement(sql3);
                st3.setString(1, String.format("%1$12d", Integer.parseInt(RFIDfrom) + i).replace(' ', '0'));
                st3.setString(2, productCode);
                st3.executeUpdate();
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }
        
        return true;

    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        // check role of the user (only administrator, cashier and shopManager)
        if(loggedUser==null || (!loggedUser.getRole().equals("Administrator")&&(!loggedUser.getRole().equals("ShopManager"))))
            throw new UnauthorizedException();

        if(!isOrderListUpdated)
        {
            List<Order> orders= new ArrayList<>();
            try {
                String sql="SELECT id, productCode, pricePerUnit, quantity, status FROM 'order'";
                PreparedStatement st = conn.prepareStatement(sql);
                ResultSet rs = st.executeQuery();

                while(rs.next()){
                    orders.add( new MyOrder(
                            rs.getInt("id"),
                            rs.getString("productCode"),
                            rs.getDouble("pricePerUnit"),
                            rs.getInt("quantity"),
                            rs.getString("status")));
                }
                orderList = orders;
                isOrderListUpdated = true;

            } catch (SQLException e) {
                return orders;
            }
        }
        return this.orderList;
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        else if (customerName == null || customerName.isEmpty())
            throw new InvalidCustomerNameException();
        else
        {
            try {
                // N.B. customerName must be unique
                String sql = "INSERT INTO customer(customerName, loyaltyCardId) VALUES (?, ?)";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setString(1,customerName);
                st.setString(2,"");
                if(st.executeUpdate()>0) {
                    this.isCustomerListUpdated = false;
                    return st.getGeneratedKeys().getInt(1);
                }
                else
                    return -1;
            } catch (SQLException e) {
                return -1;
            }
        }
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        else if (newCustomerName == null || newCustomerName.isEmpty())
            throw new InvalidCustomerNameException("Invalid Customer Name");
        else if ( id == null || id <= 0) {
            throw new InvalidCustomerIdException("Invalid Customer Id");
        }
        else {
            try{
                // update only customername
                if(newCustomerCard == null)
                {
                    String sql = "UPDATE customer SET customerName=? WHERE id=?";
                    PreparedStatement st = conn.prepareStatement(sql);
                    st.setString(1,newCustomerName);
                    st.setInt(2,id);
                    if(st.executeUpdate()>0)
                    {
                        this.isCustomerListUpdated = false;
                        return true;
                    }
                }
                // delete customercard from customer
                else if(newCustomerCard.isEmpty())
                {
                    String sql = "UPDATE customer SET loyaltyCardId=?, customerName=? WHERE id=?";
                    PreparedStatement st = conn.prepareStatement(sql);
                    st.setString(1,"");
                    st.setString(2,newCustomerName);
                    st.setInt(3,id);
                    if(st.executeUpdate()>0)
                    {
                        this.isCustomerListUpdated = false;
                        return true;
                    }
                    
                }
                // update customercard
                else {
                    //if (newCustomerCard.length()!=10 || !newCustomerCard.matches("[0-9]+") ) {
                    if (!newCustomerCard.matches("^[0-9]{10}$") ) {
                        throw new InvalidCustomerCardException("Invalid Customer Card");
                    }
                    String sql1 = "SELECT id FROM customer WHERE loyaltyCardId=?";
                    PreparedStatement st1 = conn.prepareStatement(sql1);
                    st1.setString(1, newCustomerCard);
                    ResultSet rs1 = st1.executeQuery();
                    //conn.commit();
                    //ResultSet rs1 = st1.getResultSet();
                    if(!rs1.next())
                    {
                        String sql = "UPDATE customer SET loyaltyCardId=?, customerName=? WHERE id=?";
                        PreparedStatement st = conn.prepareStatement(sql);
                        st.setString(1, newCustomerCard);
                        st.setString(2, newCustomerName);
                        st.setInt(3, id);
                        if(st.executeUpdate()>0)
                        {
                            this.isCustomerListUpdated = false;
                            return true;
                        }
                    }
                }
                return false;
            } catch (SQLException e){
                return false;
            }
        }
    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        this.isCustomerListUpdated = false;
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        else if ( id== null || id<=0) {
            throw new InvalidCustomerIdException();
        }
        else {
            try {
                String sql = "DELETE FROM customer WHERE id=?";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setInt(1,id);
                if(st.executeUpdate() == 0)
                    return false;

                isCustomerListUpdated = false;
                return true;
            }
            catch (SQLException e)
            {
                return false;
            }
        }
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        else if ( id == null || id <= 0) {
            throw new InvalidCustomerIdException();
        }
        else {
            try {
                //String sql = "SELECT * FROM customer AS C WHERE C.id=?";
                String sql = "SELECT C.id AS id, C.customerName as customerName , C.loyaltyCardId as loyaltyCardId, points FROM customer AS C LEFT JOIN loyaltyCard ON C.loyaltyCardId=loyaltyCard.cardId WHERE C.id=?";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setInt(1,id);
                ResultSet rs = st.executeQuery();

                if(!rs.next())
                    return null;

                return new MyCustomer(
                        rs.getInt("id"),
                        rs.getString("customerName"),
                        rs.getString("loyaltyCardId"),
                        rs.getInt("points")
                );

//                Customer cust = new it.polito.ezshop.model.Customer(
//                                rs.getInt("id"),
//                                rs.getString("customerName"),
//                                "",
//                                0);
//
//                if(rs.getString(3)==null || rs.getString(3).isEmpty())
//                    return cust;
//                String loyaltyCardTmp = rs.getString("loyaltyCardId");
//                cust.setCustomerCard(loyaltyCardTmp);
//                String sql2 = "SELECT * FROM LoyaltyCard L WHERE id=?";
//                PreparedStatement st2 = conn.prepareStatement(sql2);
//                st2.setString(1,loyaltyCardTmp);
//                ResultSet rs2 = st2.executeQuery();
//
//                if(!rs.isBeforeFirst())
//                    return cust;
//
//                cust.setPoints(rs2.getInt("points"));

                //return cust;
            } catch (SQLException e) {
                return null;
            }
        }
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        if(!this.isCustomerListUpdated) {
            List<Customer> customers = new ArrayList<>();
            try {
                String sql = "SELECT C.id AS id, customerName, loyaltyCardId, points FROM customer AS C LEFT JOIN loyaltyCard ON loyaltyCard.cardId=C.loyaltyCardId";
                PreparedStatement st = conn.prepareStatement(sql);
                ResultSet rs = st.executeQuery();

                while(rs.next()){
                    customers.add(new MyCustomer(
                            rs.getInt("id"),
                            rs.getString("customerName"),
                            rs.getString("loyaltyCardId"),
                            rs.getInt("points")
                    ));
                }
                customerList = customers;
                this.isCustomerListUpdated=true;
            } catch (SQLException e) {
                return customers;
            }
        }
        return this.customerList;
    }

    @Override
    public String createCard() throws UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        else{
            String nextId;
            int temp;
            try {
                String sql = "SELECT id FROM loyaltyCard ORDER BY id DESC LIMIT 1";
                PreparedStatement st = conn.prepareStatement(sql);
                ResultSet rs = st.executeQuery();

                if(!rs.next())
                {
                    temp = 1;
                    nextId="0000000001";
                }else
                {
                    temp = rs.getInt("id");
                    temp++;
                    nextId = String.format("%1$10d",temp).replace(' ', '0');
                }
                String sql2 = "INSERT INTO loyaltyCard(id,cardId) VALUES (?,?)";
                PreparedStatement st2 = conn.prepareStatement(sql2);
                st2.setInt(1,temp);
                st2.setString(2,nextId);
                st2.executeUpdate();
                return nextId;

            } catch (SQLException e) {
                return "";
            }
        }
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //else if (customerCard==null || customerCard.length()!=10 || !customerCard.matches("[0-9]+") ) {
        else if (customerCard == null || !customerCard.matches("^[0-9]{10}$")) {
            throw new InvalidCustomerCardException("Invalid customer card.");
        }
        else if (customerId == null || customerId <= 0) {
            throw new InvalidCustomerIdException("Invalid customer id.");
        }
        else {
            try{
                //Card already assigned
                String sql1 = "SELECT loyaltyCardId FROM customer WHERE loyaltyCardId=?";
                PreparedStatement st1 = conn.prepareStatement(sql1);
                st1.setString(1,customerCard);
                ResultSet rs1 = st1.executeQuery();
                //conn.commit();
                if(rs1.next())
                    return false;

            } catch(SQLException e) {
                return false;
            }

            try {
                //No customer
                String sql2 = "SELECT id FROM customer WHERE id=?";
                PreparedStatement st2 = conn.prepareStatement(sql2);
                st2.setInt(1,customerId);
                ResultSet rs2 = st2.executeQuery();
                //conn.commit();
                if(!rs2.next())
                    return false;
            } catch (SQLException e) {
                return false;
            }

            try {
                String sql3 = "UPDATE customer SET loyaltyCardId=? WHERE id=?";
                PreparedStatement st3 = conn.prepareStatement(sql3);
                st3.setString(1,customerCard);
                st3.setInt(2,customerId);
                int updatedRows = st3.executeUpdate();

                if(updatedRows == 0) {
                    return false;
                }

                this.isCustomerListUpdated = false;

            } catch (SQLException e) {
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //else if (customerCard==null || customerCard.equals("") || customerCard.length()!=10 || !customerCard.matches("[0-9]+") ) {
        else if (customerCard==null || !customerCard.matches("^[0-9]{10}$")) {
            throw new InvalidCustomerCardException();
        }
        else {
            try {
                String sql1 = "SELECT points FROM loyaltyCard WHERE id=?";
                PreparedStatement st1 = conn.prepareStatement(sql1);
                st1.setString(1, customerCard);
                ResultSet rs1 = st1.executeQuery();
                if(rs1.next()) {
                    if(pointsToBeAdded<0 && rs1.getInt("points")+pointsToBeAdded<0) {
                        return false;
                    }
                }
                else
                    return false;

                String sql2 = "UPDATE loyaltyCard SET points = points + ? WHERE id=? ";
                PreparedStatement st2 = conn.prepareStatement(sql2);
                st2.setInt(1,pointsToBeAdded);
                st2.setString(2,customerCard);
                int updatedRows = st2.executeUpdate();
                //conn.commit();
                if(updatedRows == 0) {
                    return false;
                }

                this.isCustomerListUpdated = false;
                return true;
            } catch(SQLException e) {
                return false;
            }
        }

    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        // check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        int tempId=2;
        try {
            String sql = "SELECT id FROM saleTransaction ORDER BY id DESC LIMIT 1";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if(rs.isBeforeFirst()) {
                rs.next();
                tempId = rs.getInt("id") + 2;
            }

            String sql2 = "INSERT INTO saleTransaction (id, discountRate, total, status) VALUES (?,0.0,0.0,'OPEN')";
            PreparedStatement st2 = conn.prepareStatement(sql2);
            st2.setInt(1, tempId);
            st2.executeUpdate();
            return st2.getGeneratedKeys().getInt(1);
        }catch(SQLException e){
            return -1;
        }
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        //check amount
        if(amount < 0)
            throw new InvalidQuantityException();

        // productCode not null, not empty
        if(productCode == null || productCode.equals("") )
            throw new InvalidProductCodeException();

        // check if productCode is valid
        if(!validateProductCode(productCode)) {
            throw new InvalidProductCodeException();
        }

        ProductType product;

        //check transaction status
        try {
            String sql = "SELECT id from SaleTransaction WHERE id=? AND status='OPEN'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,transactionId);
            ResultSet rs=st.executeQuery();
            rs.next();
            if(rs.getInt("id")!=transactionId){
                return false;
            }
        }catch(SQLException e){
            // transactionId not an existing or open transaction
            return false;
        }

        String actualRole = loggedUser.getRole();
        //check presence of the product;
        try {
            loggedUser.setRole("Administrator");
            product = getProductTypeByBarCode(productCode);
            if(product == null) {
                loggedUser.setRole(actualRole);
                return false;
            }
        } catch(Exception e) {
            loggedUser.setRole(actualRole);
            return false;
        }
        // check availability of the product
        try {
            loggedUser.setRole("Administrator");
            // eventually decrease amount of product if available
            if (!this.updateQuantity(product.getId(),-amount)){
                loggedUser.setRole(actualRole);
                return false;
            }
            isInventoryUpdated = false;
        } catch(Exception e){
            loggedUser.setRole(actualRole);
            return false;
        }

        loggedUser.setRole(actualRole);

        // insert a new product entry for a new sale transaction
        try {
            String sql2 = "INSERT INTO productEntry (transactionId, barcode, amount) VALUES (?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setInt(1, transactionId);
            st.setString(2, productCode);
            st.setInt(3, amount);
            int updatedRows = st.executeUpdate();

            return !(updatedRows == 0);
        } catch(SQLException e) {
            // update an existing product entry for an existing sale transaction
            try{
                String sql3 = "UPDATE ProductEntry SET amount=amount+? WHERE transactionId=? AND barcode=?";
                PreparedStatement st3= conn.prepareStatement(sql3);
                st3.setInt(1,amount);
                st3.setInt(2,transactionId);
                st3.setString(3, productCode);
                int updatedRows = st3.executeUpdate();

                return !(updatedRows == 0);
            } catch (SQLException e2) {
                return false;
//                try {
//                    loggedUser.setRole("Administrator");
//                    this.updateQuantity(product.getId(),amount);
//                    isInventoryUpdated = false;
//                    loggedUser.setRole(actualRole);
//                } catch(Exception e3){
//                    loggedUser.setRole(actualRole);
//                    throw new InvalidQuantityException();
//                }
            }
            //return false;
        }
    }

    @Override
    public boolean addProductToSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();

        if(RFID== null || RFID.length()!=12 || !RFID.matches("^[0-9]{12}$")) {
            throw new InvalidRFIDException();
        }

        String productCode;
        try {
            String sql = "SELECT barcode FROM product WHERE RFID=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, RFID);
            ResultSet rs = st.executeQuery();
            if(!rs.isBeforeFirst())
                // no barcode found
                return false;
            rs.next();

            productCode = rs.getString("barcode");
        } catch (Exception e) {
            return false;
        }

        ProductType product;

        //check transaction status
        try {
            String sql = "SELECT id from SaleTransaction WHERE id=? AND status='OPEN'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,transactionId);
            ResultSet rs=st.executeQuery();
            if(!rs.isBeforeFirst())
                return false;
        }catch(SQLException e){
            // transactionId not an existing or open transaction
            return false;
        }

        String actualRole = loggedUser.getRole();
        //check presence of the product;
        try {
            loggedUser.setRole("Administrator");
            product = getProductTypeByBarCode(productCode);
            if(product == null) {
                loggedUser.setRole(actualRole);
                return false;
            }
        } catch(Exception e) {
            loggedUser.setRole(actualRole);
            return false;
        }
        // check availability of the product
        try {
            loggedUser.setRole("Administrator");
            // eventually decrease amount of product if available
            if (!this.updateQuantity(product.getId(),-1)){
                loggedUser.setRole(actualRole);
                return false;
            }
            isInventoryUpdated = false;
        } catch(Exception e){
            loggedUser.setRole(actualRole);
            return false;
        }

        loggedUser.setRole(actualRole);

        // insert a new product entry for a new sale transaction
        try {
            String sql2 = "INSERT INTO productEntry (transactionId, barcode, amount, RFID) VALUES (?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setInt(1, transactionId);
            st.setString(2, productCode);
            st.setInt(3, 1);
            st.setString(4, RFID);
            int updatedRows = st.executeUpdate();

            return !(updatedRows == 0);
        } catch(SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        //check amount
        if(amount < 0)
            throw new InvalidQuantityException();

        // productCode not null, not empty
        if(productCode == null|| productCode.equals("") )
            throw new InvalidProductCodeException();

        // check if productCode is valid
        if(!validateProductCode(productCode)) {
            throw new InvalidProductCodeException();
        }

        ProductType product;

        //check transaction status
        try {
            String sql = "SELECT id, status from SaleTransaction WHERE id=? AND status='OPEN'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,transactionId);
            ResultSet rs = st.executeQuery();
            if(!rs.next())
                return false;
//            if(rs.getInt("id") != transactionId){
//                return false;
//            }
        }catch(SQLException e){
            // transactionId not an existing or open transaction
            return false;
        }

        String actualRole = loggedUser.getRole();
        //check presence of the product;
        try {
            loggedUser.setRole("Administrator");
            product = getProductTypeByBarCode(productCode);
            if(product == null) {
                loggedUser.setRole(actualRole);
                return false;
            }
        } catch(Exception e) {
            loggedUser.setRole(actualRole);
            return false;
        }

        loggedUser.setRole(actualRole);

        //update quantity
        try {
            String sql2 = "UPDATE productEntry SET amount=amount-? WHERE transactionId=? AND barcode=?";
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setInt(1,amount);
            st.setInt(2,transactionId);
            st.setString(3,productCode);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                return false;
            // check availability of the product
            try {
                loggedUser.setRole("Administrator");
                // eventually increase amount of product available
                if (!this.updateQuantity(product.getId(), amount)){
                    loggedUser.setRole(actualRole);
                    isInventoryUpdated = false;
                    return false;
                }
                isInventoryUpdated = false;
            } catch(Exception e) {
                loggedUser.setRole(actualRole);
                return false;
            }

            // delete row if productentry amount is 0
            String sql3 = "DELETE FROM productEntry WHERE amount=0 AND transactionId=? AND barcode=?";
            PreparedStatement st2 = conn.prepareStatement(sql3);
            st2.setInt(1,transactionId);
            st2.setString(2,productCode);
            st2.executeUpdate();

            return true;
//            String oldRole= loggedUser.getRole();
//            try {
//                loggedUser.setRole("Administrator");
//                ProductType product = this.getProductTypeByBarCode(productCode);
//                this.updateQuantity(product.getId(), amount);
//                loggedUser.setRole(oldRole);
//            }catch (Exception e2) {
//                loggedUser.setRole(oldRole);
//                return false;
//            }
        }catch(SQLException e){
            return false;
        }
    }

    @Override
    public boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();

        if(RFID== null || RFID.length()!=12 || !RFID.matches("^[0-9]{12}$")) {
            throw new InvalidRFIDException();
        }

        String productCode;
        try {
            String sql = "SELECT barcode FROM product WHERE RFID=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, RFID);
            ResultSet rs = st.executeQuery();
            if(!rs.isBeforeFirst())
                // no barcode found
                return false;
            rs.next();

            productCode = rs.getString("barcode");
        } catch (Exception e) {
            return false;
        }

        ProductType product;

        //check transaction status
        try {
            String sql = "SELECT id, status from SaleTransaction WHERE id=? AND status='OPEN'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,transactionId);
            ResultSet rs = st.executeQuery();
            if(!rs.next())
                return false;
//            if(rs.getInt("id") != transactionId){
//                return false;
//            }
        }catch(SQLException e){
            // transactionId not an existing or open transaction
            return false;
        }

        String actualRole = loggedUser.getRole();
        //check presence of the product;
        try {
            loggedUser.setRole("Administrator");
            product = getProductTypeByBarCode(productCode);
            if(product == null) {
                loggedUser.setRole(actualRole);
                return false;
            }
        } catch(Exception e) {
            loggedUser.setRole(actualRole);
            return false;
        }

        loggedUser.setRole(actualRole);

        //update quantity
        try {
            // delete row if productEntry amount is 0
            String sql3 = "DELETE FROM productEntry WHERE transactionId=? AND barcode=? AND RFID=?";
            PreparedStatement st2 = conn.prepareStatement(sql3);
            st2.setInt(1,transactionId);
            st2.setString(2,productCode);
            st2.setString(3,RFID);
            st2.executeUpdate();

            loggedUser.setRole("Administrator");
            // eventually increase amount of product available in inventory
            if (!this.updateQuantity(product.getId(), 1)){
                loggedUser.setRole(actualRole);
                isInventoryUpdated = false;
                return false;
            }
            isInventoryUpdated = false;
            return true;

        }catch(Exception e){
            loggedUser.setRole(actualRole);
            return false;
        }
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        //check discountRate
        if(discountRate >= 1.0 || discountRate < 0)
            throw new InvalidDiscountRateException("Invalid DiscountRate");

        // productCode not null, not empty
        if(productCode == null || productCode.equals("") )
            throw new InvalidProductCodeException();

        // check if productCode is valid
        if(!validateProductCode(productCode)) {
            throw new InvalidProductCodeException();
        }

        ProductType product;

        //check transaction status
        try {
            String sql = "SELECT status FROM SaleTransaction WHERE id=? AND status='OPEN'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, transactionId);
            ResultSet rs = st.executeQuery();

            if(!rs.isBeforeFirst())
                // no transaction id open
                return false;
            //if(!rs.getString("status").equals("OPEN"))
            //    return false;
        }catch (SQLException e) {
            return false;
        }

//        //check productCode
//        String sql1 = "SELECT barcode from productEntry WHERE transactionId=? AND barcode=?";
//        try {
//            PreparedStatement st = conn.prepareStatement(sql1);
//            st.setInt(1,transactionId);
//            st.setString(2,productCode);
//            ResultSet rs=st.executeQuery();
//            if(!rs.getString("barcode").equals(productCode)){
//                return false;
//            }
//
//        }catch(SQLException e){
//            return false;
//        }

        String actualRole = loggedUser.getRole();
        //check presence of the product;
        try {
            loggedUser.setRole("Administrator");
            product = getProductTypeByBarCode(productCode);
            if(product == null) {
                loggedUser.setRole(actualRole);
                return false;
            }
        } catch(Exception e) {
            loggedUser.setRole(actualRole);
            return false;
        }

        try {
            String sql2 ="UPDATE productEntry SET discountRate=? WHERE transactionId=? AND barcode=?";
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setDouble(1,discountRate);
            st.setInt(2,transactionId);
            st.setString(3,productCode);
            int updatedRows = st.executeUpdate();

            return !(updatedRows == 0);
        }catch(SQLException e){
            return false;
        }
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        // check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        // check id
        if(transactionId==null||transactionId<=0)
            throw new InvalidTransactionIdException();
        // check discountRate
        if(discountRate>=1.0||discountRate<0.0)
            throw new InvalidDiscountRateException();

        // check sale status
        try {
            String sql = "SELECT status FROM saleTransaction WHERE id=? AND status!='PAYED'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, transactionId);
            ResultSet rs = st.executeQuery();
//            if(rs.getString("status").equals("PAYED"))
//                return false;
            if(!rs.isBeforeFirst())
                return false;
        } catch (SQLException e) {
            return false;
        }

        try {
            String sql2 ="UPDATE saleTransaction SET discountRate=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setDouble(1, discountRate);
            st.setInt(2, transactionId);
            int updatedRows = st.executeUpdate();

            return !(updatedRows == 0);
        } catch(SQLException e) {
            // transaction does not exist
            return false;
        }
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId == null || transactionId<=0)
            throw new InvalidTransactionIdException();
        try {
            String sql = "SELECT PE.amount AS amount, PE.discountRate AS PEdiscountRate, PT.pricePerUnit AS pricePerUnit, ST.discountRate AS STdiscountRate FROM productEntry PE,saleTransaction ST, productType PT WHERE ST.id=PE.transactionId AND ST.id=? AND PE.barcode=PT.productCode";
            double priceWithoutSaleDiscount=0.0;
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, transactionId);
            ResultSet rs = st.executeQuery();
            double discountRate=0.0;

            if(rs.isClosed())
                return -1;

            while(rs.next()){
                discountRate = rs.getDouble("STdiscountRate");
                priceWithoutSaleDiscount+=rs.getInt("amount")*rs.getDouble("pricePerUnit")
                        -(rs.getInt("amount")*rs.getDouble("pricePerUnit"))*rs.getDouble("PEdiscountRate");
            }
            double finalPrice = priceWithoutSaleDiscount - priceWithoutSaleDiscount*discountRate;
            return (int) (finalPrice/10); // points
        }catch(SQLException e){
            return -1;
        }

    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        //check authorization 
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();

        // check sale status
        try {
            String sql2 = "SELECT id FROM saleTransaction WHERE id=? AND status='OPEN'";
            PreparedStatement st2 = conn.prepareStatement(sql2);
            st2.setInt(1, transactionId);
            ResultSet rs2 = st2.executeQuery();

//            while(rs2.next()) {
//                if(rs2.getString("status").equals("PAYED") || rs2.getString("status").equals("CLOSED"))
//                    return false;
//            }
            if(!rs2.isBeforeFirst())
                return false;

        } catch (SQLException e) {
            return false;
        }
        
        //compute total price
        double total;
        try {
            String sql = "SELECT PE.amount, PE.discountRate AS PEDiscountRate, PT.pricePerUnit, ST.discountRate AS saleDiscountRate, ST.status FROM productEntry PE,saleTransaction ST, productType PT WHERE ST.id=PE.transactionId AND ST.id=? AND PE.barcode=PT.productCode";
            double priceWithoutSaleDiscount = 0.0;
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,transactionId);
            ResultSet rs = st.executeQuery();
//            if(rs.getString("status").equals("PAYED") || rs.getString("status").equals("CLOSED"))
//                return false;
            double stDiscountR = 0;
            while(rs.next()){
                stDiscountR = rs.getDouble("saleDiscountRate");
                priceWithoutSaleDiscount += rs.getInt("amount")*rs.getDouble("pricePerUnit")
                        - (rs.getInt("amount")*rs.getDouble("pricePerUnit"))*rs.getDouble("PEDiscountRate");
            }
            total = priceWithoutSaleDiscount - priceWithoutSaleDiscount*stDiscountR;

        }catch(SQLException e){
            return false;
        }
        
        // update transaction by setting the status and its total
        try {
            String sql3 = "UPDATE saleTransaction SET status='CLOSED', total=? WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql3);
            st.setDouble(1,total);
            st.setInt(2,transactionId);
            int updatedRows = st.executeUpdate();

            return !(updatedRows == 0);
        } catch(SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(saleNumber==null||saleNumber<=0)
            throw new InvalidTransactionIdException();

        //check status
        try {
            String sql="SELECT status FROM saleTransaction WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, saleNumber);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                if(rs.getString("status").equals("PAYED"))
                    return false;
            }
        } catch(SQLException e) {
            return false;
        }

        //restore quantity
        String oldRole = loggedUser.getRole();
        try {
            String sql3 = "SELECT amount, barcode FROM productEntry WHERE transactionId=?";
            PreparedStatement st = conn.prepareStatement(sql3);
            st.setInt(1, saleNumber);
            ResultSet rs = st.executeQuery();

            loggedUser.setRole("Administrator");
            while(rs.next()){
                try {
                    if (!this.updateQuantity(this.getProductTypeByBarCode(rs.getString("barcode")).getId(), rs.getInt("amount"))) {
                        loggedUser.setRole(oldRole);
                        return false;
                    }
                } catch(Exception e) {
                    loggedUser.setRole(oldRole);
                    return false;
                }
            }
            loggedUser.setRole(oldRole);
        } catch(SQLException e) {
            loggedUser.setRole(oldRole);
            return false;
        }

        // delete transaction
        try {
            String sql2="DELETE FROM saleTransaction WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setInt(1,saleNumber);
            int deletedRows = st.executeUpdate();

            if(deletedRows == 0)
                return false;

        }catch(SQLException e){
            return false;
        }

        // delete productentry for transaction
        try {
            String sql4 = "DELETE FROM productEntry WHERE transactionId=?";
            PreparedStatement st = conn.prepareStatement(sql4);
            st.setInt(1,saleNumber);
            st.executeUpdate();
            //int deletedRows = st.executeUpdate();

            //if(deletedRows == 0)
            // no error needed if there are no products to delete
            //    return false;

        }catch(SQLException e){
            return false;
        }
        return true;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();

        List<TicketEntry> entries = new ArrayList<>();
        try {
            // create the saleTransaction, only if it is CLOSED (or PAYED in case of returnTransaction)
            //if(rs.isClosed())
            String sql2 = "SELECT ST.id, ST.discountRate, ST.total FROM saleTransaction ST WHERE ST.id=? AND status!='OPEN'";
            PreparedStatement st2 = conn.prepareStatement(sql2);
            st2.setInt(1, transactionId);
            ResultSet rs2 = st2.executeQuery();
            if(!rs2.next())
                return null;
            int id = rs2.getInt("id");
            double discountRate = rs2.getDouble("discountRate");
            double total = rs2.getDouble("total");

            // create the list of ticketEntries to put inside saleTransaction
            String sql="SELECT ST.id, ST.discountRate AS STDiscountRate, ST.total, PE.barcode AS barcode, PE.amount, PE.discountRate AS PEDiscountRate, PT.description AS description, PT.pricePerUnit AS pricePerUnit FROM saleTransaction ST, productEntry PE, productType PT WHERE PE.transactionId=ST.id AND ST.id=? AND PE.barcode=PT.productCode";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, transactionId);
            ResultSet rs = st.executeQuery();
            //if(!rs.isBeforeFirst())
                //return null;
            while(rs.next()){
                entries.add(new MyTicketEntry(
                        rs.getString("barcode"),
                        rs.getString("description"),
                        rs.getInt("amount"),
                        rs.getDouble("pricePerUnit"),
                        rs.getDouble("PEDiscountRate")
                ));
            }

            //rs2.next();
            //if (rs.isClosed()) {
            // if no product entry is found, return a saleTransaction with empty "entries" list
            return new MySaleTransaction(id, entries, discountRate, total);
            //}
        } catch(SQLException e) {
            return null;
        }
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(saleNumber == null || saleNumber <= 0)
            throw new InvalidTransactionIdException();
        //int res;
        int tempId=1;
        //check existence of a payed SaleTransaction
        try {
            String sql2 = "SELECT id FROM saleTransaction WHERE id=? AND status='PAYED'";
            PreparedStatement st2 = conn.prepareStatement(sql2);
            st2.setInt(1,saleNumber);
            ResultSet rs2 = st2.executeQuery();
            if(!rs2.isBeforeFirst()){
                return -1;
            }
        } catch(SQLException e) {
            return -1;
        }

        // create a new and empty return transaction
        try {
            String sql = "SELECT id FROM returnTransaction ORDER BY id DESC LIMIT 1";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if(rs.isBeforeFirst())
            {
                rs.next();
                tempId=rs.getInt("id")+2;
            }
            String sql2 = "INSERT INTO returnTransaction (id, saleTransactionId,discountRate,total,status) VALUES (?,?,0.0,0.0,'OPEN')";
            PreparedStatement st2 = conn.prepareStatement(sql2);
            st2.setInt(1,tempId);
            st2.setInt(2,saleNumber);
            st2.executeUpdate();
            //res= st.getGeneratedKeys().getInt(1);
            return st2.getGeneratedKeys().getInt(1);
        }catch(SQLException e){
            return -1;
        }
        //return res;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException{
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException();
        //check quantity
        if(amount <= 0)
            throw new InvalidQuantityException();

        // productCode not null, not empty
        if(productCode == null || productCode.equals("") )
            throw new InvalidProductCodeException();

        // check if productCode is valid
        if(!validateProductCode(productCode)) {
            throw new InvalidProductCodeException();
        }

        // check if return transaction exists and returns its saleTransactionId
        int saleTransactionId;
        try {
            String sql = "SELECT saleTransactionId FROM returnTransaction WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,returnId);

            ResultSet rs = st.executeQuery();
            if(!rs.next())
                return false;
            saleTransactionId = rs.getInt("saleTransactionId");

        } catch(SQLException e) {
            return false;
        }

        // check if there is the product and the proper quantity in the sale transaction
        double discountOfProduct;
        int saleAmount;
        try {
            String sql3 = "SELECT amount, discountRate FROM productEntry WHERE transactionId=? AND barcode=?";
            PreparedStatement st3 = conn.prepareStatement(sql3);
            st3.setInt(1,saleTransactionId);
            st3.setString(2,productCode);

            ResultSet rs3 = st3.executeQuery();

            if(!rs3.next())
                return false;
            discountOfProduct = rs3.getDouble("discountRate");
            saleAmount = rs3.getInt("amount");
            if(amount > saleAmount)
                return false;
        }catch(SQLException e) {
            return false;
        }

        // check total amount of the return transaction
        try {
            String sql5 = "SELECT amount FROM productEntry WHERE transactionId=? AND barcode=?";
            PreparedStatement st5 = conn.prepareStatement(sql5);
            st5.setInt(1,returnId);
            st5.setString(2,productCode);
            ResultSet rs5 = st5.executeQuery();

            if(rs5.next())
                if(amount + rs5.getInt("amount") > saleAmount)
                    return false;
        }catch(SQLException e) {
            return false;
        }

        // insert a new productEntry for returnTransaction if not yet created
        try {
            String sql2 = "INSERT INTO productEntry (transactionId, barcode, amount, discountRate) VALUES (?,?,?,?) ";
            PreparedStatement st2 = conn.prepareStatement(sql2);
            st2.setInt(1,returnId);
            st2.setString(2,productCode);
            st2.setInt(3,amount);
            st2.setDouble(4,discountOfProduct);

            int updatedRows = st2.executeUpdate();
            return !(updatedRows == 0);

        }catch(SQLException e) {
            // update an existing product entry for an existing return transaction
            try {
                String sql4 = "UPDATE productEntry SET amount=amount+? WHERE transactionId=? AND barcode=?";
                PreparedStatement st4 = conn.prepareStatement(sql4);
                st4.setInt(1,amount);
                st4.setInt(2,returnId);
                st4.setString(3,productCode);
                int updatedRows = st4.executeUpdate();

                return !(updatedRows == 0);

            } catch(SQLException e2) {
                    return false;
            }
            //return false;
        }

    }

    @Override
    public boolean returnProductRFID(Integer returnId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException();

        if(RFID== null || RFID.length()!=12 || !RFID.matches("^[0-9]{12}$")) {
            throw new InvalidRFIDException();
        }

        // check if return transaction exists and returns its saleTransactionId
        int saleTransactionId;
        try {
            String sql = "SELECT saleTransactionId FROM returnTransaction WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,returnId);

            ResultSet rs = st.executeQuery();
            if(!rs.next())
                return false;
            saleTransactionId = rs.getInt("saleTransactionId");

        } catch(SQLException e) {
            return false;
        }

        String productCode;
        try {
            String sql = "SELECT product.barcode FROM product, productEntry WHERE product.RFID=? AND productEntry.transactionId=? AND product.RFID=productEntry.RFID";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, RFID);
            st.setInt(2,saleTransactionId);
            ResultSet rs = st.executeQuery();
            if(!rs.isBeforeFirst())
                // no barcode found
                return false;
            rs.next();

            productCode = rs.getString("barcode");
        } catch (Exception e) {
            return false;
        }

        // check if there is the product and the proper quantity in the sale transaction
        double discountOfProduct;
        try {
            String sql3 = "SELECT discountRate FROM productEntry WHERE transactionId=? AND barcode=? AND RFID=?";
            PreparedStatement st3 = conn.prepareStatement(sql3);
            st3.setInt(1,saleTransactionId);
            st3.setString(2,productCode);
            st3.setString(3,RFID);
            ResultSet rs3 = st3.executeQuery();

            if(!rs3.isBeforeFirst())
                return false;
            rs3.next();
            discountOfProduct = rs3.getDouble("discountRate");
        }catch(SQLException e) {
            return false;
        }

        // insert a new productEntry for returnTransaction if not yet created
        try {
            String sql2 = "INSERT INTO productEntry (transactionId, barcode, amount, discountRate, RFID) VALUES (?,?,?,?,?)";
            PreparedStatement st2 = conn.prepareStatement(sql2);
            st2.setInt(1,returnId);
            st2.setString(2,productCode);
            st2.setInt(3,1);
            st2.setDouble(4,discountOfProduct);
            st2.setString(5, RFID);

            int updatedRows = st2.executeUpdate();
            return !(updatedRows == 0);

        }catch(SQLException e) {
            return false;
        }
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(returnId==null||returnId<=0)
            throw new InvalidTransactionIdException();

        if(!commit){
            return this.deleteReturnTransaction(returnId);
        }

        //check status

        String productCode;
        int amount=0;
        int idSaleTransaction;
        ProductType product=null;
        String oldRole=loggedUser.getRole();
        try {
            String sql5="SELECT PE.barcode AS barcode, amount, RT.saleTransactionId AS saleTransactionId FROM returnTransaction RT, productEntry PE WHERE RT.id=? AND PE.transactionId=RT.id AND status='OPEN'";
            PreparedStatement st5 = conn.prepareStatement(sql5);
            st5.setInt(1, returnId);
            ResultSet rs5 = st5.executeQuery();

            if(!rs5.isBeforeFirst())
                return false;

            while(rs5.next()) {
                productCode = rs5.getString("barcode");
                amount = rs5.getInt("amount");
                idSaleTransaction = rs5.getInt("saleTransactionId");
                loggedUser.setRole("Administrator");
                if ((product = this.getProductTypeByBarCode(productCode)) == null) {
                    loggedUser.setRole(oldRole);
                    return false;
                }

                String sql9 = "UPDATE ProductEntry SET amount=amount-? WHERE transactionId=? AND barcode=?";
                PreparedStatement st6 = conn.prepareStatement(sql9);
                st6.setInt(1, amount);
                st6.setInt(2, idSaleTransaction);
                st6.setString(3, productCode);
                int updatedRows = st6.executeUpdate();
                if (updatedRows == 0) {
                    loggedUser.setRole(oldRole);
                    return false;
                }

                if (!this.updateQuantity(product.getId(), amount)) {
                    loggedUser.setRole(oldRole);
                    return false;
                }

                // delete row if productentry amount is 0
                String sql7 = "DELETE FROM productEntry WHERE amount=0 AND transactionId=? AND barcode=?";
                PreparedStatement st7 = conn.prepareStatement(sql7);
                st7.setInt(1, idSaleTransaction);
                st7.setString(2, productCode);
                st7.executeUpdate();

                isInventoryUpdated = false;
                loggedUser.setRole(oldRole);
            }
        } catch(Exception e){
            loggedUser.setRole(oldRole);
            return false;
        }

//        ProductType product;
//        String oldRole=loggedUser.getRole();
//        try {
//            loggedUser.setRole("Administrator");
//            product = this.getProductTypeByBarCode(productCode);
//            loggedUser.setRole(oldRole);
//        }catch(Exception e) {
//            loggedUser.setRole(oldRole);
//            return false;
//        }
//
//        try{
//            loggedUser.setRole("Administrator");
//
//            //decrease productEntry of sale
//            String sql9 = "UPDATE ProductEntry SET amount=amount-? WHERE transactionId=? AND barcode=?";
//            try{
//                PreparedStatement st5 = conn.prepareStatement(sql9);
//                st5.setInt(1,amount);
//                st5.setInt(2,idSaleTransaction);
//                st5.setString(3,productCode);
//                st5.executeUpdate();
//
//                this.updateQuantity(product.getId(),amount);
//            }catch (SQLException e){e.printStackTrace();}
//            loggedUser.setRole(oldRole);
//        }catch(Exception e) {
//            loggedUser.setRole(oldRole);
//            return false;
//        }

        double discountOfSale;
        //get discountRate of sale
        try{
            String sql4="SELECT ST.discountRate AS rate FROM returnTransaction RT, saleTransaction ST WHERE ST.id=RT.saleTransactionId AND RT.id=?";
            PreparedStatement st = conn.prepareStatement(sql4);
            st.setInt(1,returnId);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                return false;

            discountOfSale=rs.getDouble("rate");
        }catch(SQLException e){
            return false;
        }

        //discount rate products
        double total=0;
        try{
            String sql3="SELECT PE.amount AS amount, PT.pricePerUnit AS pricePerUnit, PE.discountRate AS discountRate FROM productEntry PE, productType PT WHERE PE.barcode=PT.productCode AND PE.transactionId=?";
            PreparedStatement st = conn.prepareStatement(sql3);
            st.setInt(1,returnId);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                double price = rs.getDouble("pricePerUnit");
                price = price - price*discountOfSale;
                total+=rs.getInt("amount")* price  - (price* (rs.getDouble("discountRate"))*rs.getInt("amount"));
            }

        }catch(SQLException e){
            return false;
        }

        // update transaction by setting the status
        try{
            String sql2 = "UPDATE returnTransaction SET status='CLOSED', total=?, discountRate=?  WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setDouble(1, total);
            st.setDouble(2, discountOfSale);
            st.setInt(3,returnId);
            int updatedRows = st.executeUpdate();
            return !(updatedRows == 0);
        }catch(SQLException e){
            try{
                loggedUser.setRole("Administrator");
                if(product != null)
                    this.updateQuantity(product.getId(),-amount);
                loggedUser.setRole(oldRole);
                return false;
            }catch(Exception ee){
                loggedUser.setRole(oldRole);
                return false;
            }
        }
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(returnId==null||returnId<=0)
            throw new InvalidTransactionIdException();

        //check status
        try {
            String sql = "SELECT status FROM returnTransaction WHERE id=? AND status!='PAYED'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,returnId);
            ResultSet rs = st.executeQuery();
            if(!rs.isBeforeFirst())
                return false;
        }catch(SQLException e){
            return false;
        }

        try {
            String sql="DELETE FROM productEntry WHERE transactionId=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,returnId);
            st.executeUpdate();

        }catch(SQLException e){
            return false;
        }

        try {
            String sql="DELETE FROM returnTransaction WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,returnId);
            st.executeUpdate();

        }catch(SQLException e){
            return false;
        }

        return true;

    }

    @Override
    public double receiveCashPayment(Integer transactionId, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId==null||transactionId<=0)
            throw new InvalidTransactionIdException("Invalid Transaction Id");
        //check cash
        if(cash <= 0)
            throw new InvalidPaymentException("Invalid Payment");

        double total;
        try {
            String sql="SELECT total FROM saleTransaction WHERE id=? AND status='CLOSED'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,transactionId);
            ResultSet rs = st.executeQuery();
            rs.next();
            total = rs.getDouble("total");
        }catch(SQLException e){
            return -1.0;
        }

        if(total>cash){
            return -1.0;
        }else{
            String actualRole= loggedUser.getRole();
            loggedUser.setRole("Administrator");
            this.recordBalanceUpdate(total);
            loggedUser.setRole(actualRole);
            // update transaction by setting the status
            try{
                String sql2 = "UPDATE saleTransaction SET status='PAYED' WHERE id=?";
                PreparedStatement st = conn.prepareStatement(sql2);
                st.setInt(1,transactionId);
                int updatedRows = st.executeUpdate();

                if (updatedRows>0)
                    return cash-total;

            }catch(SQLException e) {
                return -1.0;
            }
        }
        return -1.0;
    }

    @Override
    public boolean receiveCreditCardPayment(Integer transactionId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(transactionId==null||transactionId<=0)
            throw new InvalidTransactionIdException();
        if(creditCard==null || creditCard.isEmpty() || !MyCreditCard.validateWithLuhn(creditCard))
            throw new InvalidCreditCardException();

        double total;
        try {
            String sql="SELECT total FROM saleTransaction WHERE id=? AND status='CLOSED'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,transactionId);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                return false;
            total = rs.getDouble("total");
        }catch(SQLException e){
            return false;
        }
/*
        String sql2="SELECT balance FROM creditCard WHERE cardNumber=?";
        try {
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setString(1,creditCard);
            ResultSet rs = st.executeQuery();
            if(rs.getDouble("balance")<total)
                return false;
        }catch(SQLException e){
            return false;
        }

        String sql3 ="UPDATE creditCard SET balance=balance-? WHERE cardNumber=?";
        try {
            PreparedStatement st = conn.prepareStatement(sql3);
            st.setDouble(1,total);
            st.setString(2,creditCard);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                return false;

        }catch(SQLException e){
            return false;
        }
*/
        try{
            String sql4 = "UPDATE saleTransaction SET status='PAYED' WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql4);
            st.setInt(1,transactionId);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                return false;
        }catch(SQLException e) {
            return false;
        }
        String oldRole=loggedUser.getRole();
        loggedUser.setRole("Administrator");
        this.recordBalanceUpdate(total);
        loggedUser.setRole(oldRole);
        return true;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        //check authorization
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(returnId==null||returnId<=0)
            throw new InvalidTransactionIdException();


        double total;
        try {
            String sql="SELECT total FROM returnTransaction WHERE id=? AND status='CLOSED'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,returnId);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                return -1.0;

            total = rs.getDouble("total");

        }catch(SQLException e){
            return -1.0;
        }

        String oldRole=loggedUser.getRole();
        loggedUser.setRole("Administrator");
        this.recordBalanceUpdate(-total);
        loggedUser.setRole(oldRole);

        // update transaction by setting the status
        try{
            String sql2 = "UPDATE returnTransaction SET status='PAYED' WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql2);
            st.setInt(1,returnId);
            int updatedRows = st.executeUpdate();

            if(updatedRows == 0)
                return -1.0;
            return total;
        }catch(SQLException e) {
            return -1.0;
        }

    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager") && !loggedUser.getRole().equals("Cashier")))
            throw new UnauthorizedException();
        //check id
        if(returnId==null||returnId<=0)
            throw new InvalidTransactionIdException();
        if(creditCard==null || creditCard.isEmpty() || !MyCreditCard.validateWithLuhn(creditCard))
            throw new InvalidCreditCardException("Invalid credit card.");

        double total;
        try {
            String sql="SELECT total FROM returnTransaction WHERE id=? AND status='CLOSED'";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,returnId);
            ResultSet rs = st.executeQuery();

            if(!rs.next())
                return -1.0;

            total = rs.getDouble("total");

        }catch(SQLException e){
            return -1.0;
        }
/*
        String sql3 ="UPDATE creditCard SET balance=balance+? WHERE cardNumber=?";
        try {
            PreparedStatement st = conn.prepareStatement(sql3);
            st.setDouble(1,total);
            st.setString(2,creditCard);
            st.executeUpdate();

        }catch(SQLException e){
            return -1.0;
        }
*/
        try{
            String sql4 = "UPDATE returnTransaction SET status='PAYED' WHERE id=?";
            PreparedStatement st = conn.prepareStatement(sql4);
            st.setInt(1,returnId);
            int updatedRows = st.executeUpdate();
            if (updatedRows == 0)
                return -1.0;
        }catch(SQLException e) {
            return -1.0;
        }

        String oldRole=loggedUser.getRole();
        loggedUser.setRole("Administrator");
        this.recordBalanceUpdate(-total);
        loggedUser.setRole(oldRole);

        return total;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException();
        String type;
        double sum = 0.0;
        if(toBeAdded<0)
            type="DEBIT";
        else
            type ="CREDIT";
        try{
            String sql = "SELECT money FROM balanceOperation";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next())
            {
                sum+=rs.getDouble("money");
            }
            if (sum+toBeAdded<0)
                return false;

            String sql2 = "INSERT INTO balanceOperation(date,money,type) VALUES (?,?,?) ";
            PreparedStatement st2 = conn.prepareStatement(sql2);
            st2.setDate(1, java.sql.Date.valueOf(LocalDate.of(LocalDate.now().getYear(),LocalDate.now().getMonthValue(),LocalDate.now().getDayOfMonth())));
            st2.setDouble(2,toBeAdded);
            st2.setString(3,type);
            int updatedRows = st2.executeUpdate();

            return !(updatedRows == 0);
        }catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        List<BalanceOperation> l = new ArrayList<>();
        ResultSet rs;
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException();
        if (to!=null && from!=null)
        {
            LocalDate realFrom=from, realTo=to;
            if (to.isBefore(from))
            {
                realFrom = to;
                realTo=from;
            }

            try {
                String sql = "SELECT id, date, money, type FROM balanceOperation WHERE date >= ? AND date <= ?";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setDate(1, java.sql.Date.valueOf(realFrom));
                st.setDate(2, java.sql.Date.valueOf(realTo));
                rs=st.executeQuery();
                while (rs.next())
                {
                    l.add(
                            new MyBalanceOperation(
                                    rs.getInt("id"),
                                    Instant.ofEpochMilli(Long.parseLong(rs.getString("date"))).atZone(ZoneId.systemDefault()).toLocalDate(),
                                    rs.getDouble("money"),
                                    rs.getString("type")
                            )
                    );
                }
            } catch (SQLException ignored) {

            }
        }
        else if(from==null && to!=null) {
            try {
                String sql = "SELECT id, date, money, type FROM balanceOperation WHERE date <= ?";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setDate(1,java.sql.Date.valueOf(to));
                rs = st.executeQuery();
                while (rs.next())
                {
                    l.add(
                            new MyBalanceOperation(
                                    rs.getInt("id"),
                                    Instant.ofEpochMilli(Long.parseLong(rs.getString("date"))).atZone(ZoneId.systemDefault()).toLocalDate(),
                                    rs.getDouble("money"),
                                    rs.getString("type")
                            )
                    );
                }
            } catch (SQLException ignored) {

            }
        }
        else if(from!=null)
        {
            try {
                String sql = "SELECT id, date, money, type FROM balanceOperation WHERE date >= ?";
                PreparedStatement st = conn.prepareStatement(sql);
                st.setDate(1, java.sql.Date.valueOf(from));

                rs = st.executeQuery();
                while (rs.next())
                {
                    l.add(
                            new MyBalanceOperation(
                                    rs.getInt("id"),
                                    Instant.ofEpochMilli(Long.parseLong(rs.getString("date"))).atZone(ZoneId.systemDefault()).toLocalDate(),
                                    rs.getDouble("money"),
                                    rs.getString("type")
                            )
                    );
                }
            } catch (SQLException ignored) {

            }
        }else {
            try {
                String sql = "SELECT id, date, money, type FROM balanceOperation";
                PreparedStatement st = conn.prepareStatement(sql);
                rs = st.executeQuery();

                while (rs.next())
                {
                    l.add(
                            new MyBalanceOperation(
                                    rs.getInt("id"),
                                    Instant.ofEpochMilli(Long.parseLong(rs.getString("date"))).atZone(ZoneId.systemDefault()).toLocalDate(),
                                    rs.getDouble("money"),
                                    rs.getString("type")
                            )
                    );
                }
                return l;
            }catch (SQLException ignored){
            }
        }
        return l;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        if(loggedUser == null || (!loggedUser.getRole().equals("Administrator") && !loggedUser.getRole().equals("ShopManager")))
            throw new UnauthorizedException();
        else {
            double sum = 0.0;
            try {
                String sql = "SELECT money FROM balanceOperation";
                PreparedStatement st = conn.prepareStatement(sql);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    sum+=rs.getDouble("money");
                }
                return sum;
            } catch (SQLException e) {
                return 0.0;
            }
        }
    }
}

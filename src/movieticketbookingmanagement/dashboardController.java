package movieticketbookingmanagement;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Dashboard Controller - Quản lý toàn bộ chức năng của ứng dụng
 */
public class dashboardController implements Initializable {

    // ===================== CONSTANTS =====================
    private static final float SPECIAL_CLASS_PRICE = 20000f;
    private static final float NORMAL_CLASS_PRICE = 10000f;
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String IMAGES_DIR = "images/movies/";
    
    // ===================== FXML COMPONENTS =====================
    // Dashboard
    @FXML private AnchorPane dashboard_form;
    @FXML private Button dashboard_btn;
    @FXML private AnchorPane dashboard_availableMovies;
    @FXML private AnchorPane dashboard_totalEarnToday;
    @FXML private AnchorPane dashboard_totalSoldTicket;
    
    // Add Movies
    @FXML private AnchorPane addMovies_form;
    @FXML private Button addMovies_btn;
    @FXML private TextField addMovies_movieTitle;
    @FXML private TextField addMovies_genre;
    @FXML private TextField addMovies_duration;
    @FXML private DatePicker addMovies_date;
    @FXML private ImageView addMovies_imageView;
    @FXML private TextField addMovies_search;
    @FXML private TableView<moviesData> addMovies_tableView;
    @FXML private TableColumn<moviesData, String> addMovies_col_movieTitle;
    @FXML private TableColumn<moviesData, String> addMovies_col_genre;
    @FXML private TableColumn<moviesData, String> addMovies_col_duration;
    @FXML private TableColumn<moviesData, String> addMovies_col_date;
    @FXML private Button addMovies_import;
    @FXML private Button addMovies_insertBtn;
    @FXML private Button addMovies_updateBtn;
    @FXML private Button addMovies_deleteBtn;
    @FXML private Button addMovies_clearBtn;
    
    // Available Movies (Ticket Booking)
    @FXML private AnchorPane availableMovies_form;
    @FXML private Button availableMovies_btn;
    @FXML private TableView<moviesData> availableMovies_tableView;
    @FXML private TableColumn<moviesData, String> availableMovies_col_movieTitle;
    @FXML private TableColumn<moviesData, String> availableMovies_col_genre;
    @FXML private TableColumn<moviesData, String> availableMovies_col_showingDate;
    @FXML private Label availableMovies_movieTitle;
    @FXML private Label availableMovies_genre;
    @FXML private Label availableMovies_date;
    @FXML private ImageView availableMovies_imageView;
    @FXML private Label availableMovies_title;
    @FXML private Spinner<Integer> availableMovies_specialClass_quantity;
    @FXML private Spinner<Integer> availableMovies_normalClass_quantity;
    @FXML private Label availableMovies_specialClass_price;
    @FXML private Label availableMovies_normalClass_price;
    @FXML private Label availableMovies_total;
    @FXML private Button availableMovies_selectMovie;
    @FXML private Button availableMovies_clearBtn;
    @FXML private Button availableMovies_buyBtn;
    @FXML private Button availableMovies_receiptBtn;
    
    // Edit Screening
    @FXML private AnchorPane editScreening_form;
    @FXML private Button editScreening_btn;
    @FXML private TableView<moviesData> addScreening_tableView;
    @FXML private TableColumn<moviesData, String> editScreening_col_movieTitle;
    @FXML private TableColumn<moviesData, String> editScreening_col_genre;
    @FXML private TableColumn<moviesData, String> editScreening_col_duration;
    @FXML private TableColumn<moviesData, String> editScreening_col_current;
    @FXML private TextField editScreening_search;
    @FXML private ImageView editScreening_imageView;
    @FXML private Label editScreening_title;
    @FXML private ComboBox<String> editScreening_current;
    @FXML private Button editScreening_update;
    @FXML private Button editScreening_delete;
    
    // Customers
    @FXML private AnchorPane customers_form;
    @FXML private Button customers_btn;
    @FXML private TableView<customerData> customer_tableView;
    @FXML private TableColumn<customerData, String> customers_col_ticketNumber;
    @FXML private TableColumn<customerData, String> customers_col_movieTitle;
    @FXML private TableColumn<customerData, String> customers_col_date;
    @FXML private TableColumn<customerData, String> customers_col_time;
    @FXML private TextField customers_search;
    @FXML private Label customers_ticketNumber;
    @FXML private Label customers_movieTitle;
    @FXML private Label customers_date;
    @FXML private Label customers_time;
    @FXML private Label customers_genre;
    @FXML private Button customers_clearBtn;
    @FXML private Button customers_deleteBtn;
    
    // Window Controls
    @FXML private Button close;
    @FXML private Button minimize;
    @FXML private FontAwesomeIconView signout;
    @FXML private Label username;
    
    // ===================== PRIVATE VARIABLES =====================
    private Image image;
    private double x = 0;
    private double y = 0;
    
    // Observable Lists cho các table
    private ObservableList<moviesData> moviesList;
    private ObservableList<moviesData> availableMoviesList;
    private ObservableList<moviesData> editScreeningList;
    private ObservableList<customerData> customersList;
    
    // Spinners cho ticket booking
    private SpinnerValueFactory<Integer> spinner1;
    private SpinnerValueFactory<Integer> spinner2;
    
    // Biến lưu thông tin booking hiện tại
    private float price1 = 0;
    private float price2 = 0;
    private float total = 0;
    private int qty1 = 0;
    private int qty2 = 0;
    
    // Lưu thông tin transaction cuối cùng cho receipt
    private TransactionInfo lastTransaction = new TransactionInfo();
    
    // ===================== INNER CLASS CHO TRANSACTION =====================
    private class TransactionInfo {
        String movieTitle = "";
        String ticketType = "";
        int qty1 = 0;
        int qty2 = 0;
        float price1 = 0;
        float price2 = 0;
        float total = 0;
        String transactionTime = "";
    }
    
    // ===================== INITIALIZATION =====================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup ban đầu
        displayUsername();
        setupDatePickerValidation();
        setupSpinners();
        setupComboBoxes();
        
        // Load data cho các table
        loadAllData();
        
        // Hiển thị dashboard mặc định
        switchToDashboard();
    }
    
    // Setup username hiển thị
    private void displayUsername() {
        if (getData.username != null) {
            username.setText(getData.username);
        }
    }
    
    // Setup validation cho date picker
    private void setupDatePickerValidation() {
        // Chỉ cho phép chọn từ ngày hiện tại trở đi
        addMovies_date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }
    
    // Setup spinners cho ticket booking
    private void setupSpinners() {
        spinner1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        spinner2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        
        availableMovies_specialClass_quantity.setValueFactory(spinner1);
        availableMovies_normalClass_quantity.setValueFactory(spinner2);
        
        // Auto update giá khi thay đổi số lượng
        availableMovies_specialClass_quantity.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        availableMovies_normalClass_quantity.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }
    
    // Setup combo boxes
    private void setupComboBoxes() {
        ObservableList<String> statusList = FXCollections.observableArrayList(
            "Bắt đầu chiếu", "Ngừng chiếu"
        );
        editScreening_current.setItems(statusList);
    }
    
    // Load tất cả data
    private void loadAllData() {
        loadMoviesData();
        loadAvailableMoviesData();
        loadEditScreeningData();
        loadCustomersData();
    }
    
    // ===================== NAVIGATION METHODS =====================
    // Chuyển đổi giữa các form
    @FXML
    public void switchForm(ActionEvent event) {
        resetFormStyles();
        
        if (event.getSource() == dashboard_btn) {
            switchToDashboard();
        } else if (event.getSource() == addMovies_btn) {
            switchToAddMovies();
        } else if (event.getSource() == availableMovies_btn) {
            switchToAvailableMovies();
        } else if (event.getSource() == editScreening_btn) {
            switchToEditScreening();
        } else if (event.getSource() == customers_btn) {
            switchToCustomers();
        }
    }
    
    // Reset style của tất cả forms
    private void resetFormStyles() {
        dashboard_form.setVisible(false);
        addMovies_form.setVisible(false);
        availableMovies_form.setVisible(false);
        editScreening_form.setVisible(false);
        customers_form.setVisible(false);
        
        dashboard_btn.setStyle("-fx-background-color: transparent");
        addMovies_btn.setStyle("-fx-background-color: transparent");
        availableMovies_btn.setStyle("-fx-background-color: transparent");
        editScreening_btn.setStyle("-fx-background-color: transparent");
        customers_btn.setStyle("-fx-background-color: transparent");
    }
    
    // Các method switch cho từng form
    private void switchToDashboard() {
        dashboard_form.setVisible(true);
        dashboard_btn.setStyle("-fx-background-color: #ae2d3c;");
    }
    
    private void switchToAddMovies() {
        addMovies_form.setVisible(true);
        addMovies_btn.setStyle("-fx-background-color: #ae2d3c;");
        loadMoviesData();
    }
    
    private void switchToAvailableMovies() {
        availableMovies_form.setVisible(true);
        availableMovies_btn.setStyle("-fx-background-color: #ae2d3c;");
        loadAvailableMoviesData();
    }
    
    private void switchToEditScreening() {
        editScreening_form.setVisible(true);
        editScreening_btn.setStyle("-fx-background-color: #ae2d3c;");
        loadEditScreeningData();
    }
    
    private void switchToCustomers() {
        customers_form.setVisible(true);
        customers_btn.setStyle("-fx-background-color: #ae2d3c;");
        loadCustomersData();
    }
    
    // ===================== MOVIES MANAGEMENT =====================
    // Load danh sách phim
    private void loadMoviesData() {
        moviesList = FXCollections.observableArrayList();
        
        String sql = "SELECT * FROM movie ORDER BY id DESC";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                moviesData movie = new moviesData(
                    rs.getInt("id"),
                    rs.getString("movieTitle"),
                    rs.getString("genre"),
                    rs.getString("duration"),
                    rs.getString("image"),
                    rs.getDate("date"),
                    rs.getString("current")
                );
                moviesList.add(movie);
            }
            
            // Setup table columns
            addMovies_col_movieTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            addMovies_col_genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
            addMovies_col_duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
            addMovies_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
            
            // Setup search
            setupMoviesSearch();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể tải danh sách phim: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Setup search cho movies table
    private void setupMoviesSearch() {
        FilteredList<moviesData> filteredData = new FilteredList<>(moviesList, p -> true);
        
        addMovies_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(movie -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase().trim();
                
                return (movie.getTitle() != null && movie.getTitle().toLowerCase().contains(lowerCaseFilter))
                    || (movie.getGenre() != null && movie.getGenre().toLowerCase().contains(lowerCaseFilter))
                    || (movie.getDuration() != null && movie.getDuration().toLowerCase().contains(lowerCaseFilter));
            });
        });
        
        SortedList<moviesData> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(addMovies_tableView.comparatorProperty());
        addMovies_tableView.setItems(sortedData);
    }
    
    // Import ảnh cho phim
    @FXML
    public void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh");
        fileChooser.getExtensionFilters().add(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        Stage stage = (Stage) addMovies_form.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                // Lưu ảnh vào thư mục của app
                String savedPath = saveImageToAppDirectory(file);
                
                // Hiển thị ảnh
                image = new Image(file.toURI().toString(), 182, 205, false, true);
                addMovies_imageView.setImage(image);
                
                // Lưu path
                getData.path = savedPath;
                
            } catch (Exception e) {
                showAlert("Lỗi", "Không thể import hình ảnh: " + e.getMessage(), AlertType.ERROR);
            }
        }
    }
    
    // Lưu ảnh vào thư mục app
    private String saveImageToAppDirectory(File sourceFile) throws Exception {
        // Tạo thư mục nếu chưa có
        File destDir = new File(IMAGES_DIR);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        // Tạo tên file unique
        String fileName = UUID.randomUUID() + "_" + sourceFile.getName();
        File destFile = new File(IMAGES_DIR + fileName);
        
        // Copy file
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        return destFile.getAbsolutePath();
    }
    
    // Thêm phim mới
    @FXML
    public void insertAddMovies() {
        if (!validateMovieInput()) {
            return;
        }
        
        String sql = "INSERT INTO movie(movieTitle, genre, duration, image, date, current) VALUES(?,?,?,?,?,?)";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Check phim đã tồn tại chưa
            if (isMovieExists(conn, addMovies_movieTitle.getText().trim())) {
                showAlert("Lỗi", "Phim đã tồn tại trong hệ thống!", AlertType.ERROR);
                return;
            }
            
            pstmt.setString(1, addMovies_movieTitle.getText().trim());
            pstmt.setString(2, addMovies_genre.getText().trim());
            pstmt.setString(3, addMovies_duration.getText().trim());
            pstmt.setString(4, getData.path);
            pstmt.setDate(5, java.sql.Date.valueOf(addMovies_date.getValue()));
            pstmt.setString(6, "Showing");
            
            pstmt.executeUpdate();
            
            showAlert("Thành công", "Thêm phim mới thành công!", AlertType.INFORMATION);
            clearAddMoviesForm();
            loadMoviesData();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể thêm phim: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Update phim
    @FXML
    public void updateAddMovies() {
        moviesData selectedMovie = addMovies_tableView.getSelectionModel().getSelectedItem();
        
        if (selectedMovie == null) {
            showAlert("Lỗi", "Vui lòng chọn phim cần cập nhật!", AlertType.ERROR);
            return;
        }
        
        if (!validateMovieInput()) {
            return;
        }
        
        String sql = "UPDATE movie SET movieTitle=?, genre=?, duration=?, image=?, date=? WHERE id=?";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, addMovies_movieTitle.getText().trim());
            pstmt.setString(2, addMovies_genre.getText().trim());
            pstmt.setString(3, addMovies_duration.getText().trim());
            pstmt.setString(4, getData.path);
            pstmt.setDate(5, java.sql.Date.valueOf(addMovies_date.getValue()));
            pstmt.setInt(6, selectedMovie.getId());
            
            pstmt.executeUpdate();
            
            showAlert("Thành công", "Cập nhật phim thành công!", AlertType.INFORMATION);
            clearAddMoviesForm();
            loadMoviesData();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể cập nhật phim: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Xóa phim
    @FXML
    public void deleteAddMovies() {
        moviesData selectedMovie = addMovies_tableView.getSelectionModel().getSelectedItem();
        
        if (selectedMovie == null) {
            showAlert("Lỗi", "Vui lòng chọn phim cần xóa!", AlertType.ERROR);
            return;
        }
        
        // Confirm xóa
        if (!showConfirmDialog("Xác nhận xóa", 
            "Bạn có chắc chắn muốn xóa phim: " + selectedMovie.getTitle() + "?")) {
            return;
        }
        
        String sql = "DELETE FROM movie WHERE id = ?";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, selectedMovie.getId());
            pstmt.executeUpdate();
            
            showAlert("Thành công", "Xóa phim thành công!", AlertType.INFORMATION);
            clearAddMoviesForm();
            loadMoviesData();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể xóa phim: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Clear form add movies
    @FXML
    public void clearAddMoviesList() {
        clearAddMoviesForm();
    }
    
    private void clearAddMoviesForm() {
        addMovies_movieTitle.clear();
        addMovies_genre.clear();
        addMovies_duration.clear();
        addMovies_date.setValue(null);
        addMovies_imageView.setImage(null);
        getData.path = null;
        addMovies_tableView.getSelectionModel().clearSelection();
    }
    
    // Select movie từ table
    @FXML
    public void selectAddMoviesList() {
        moviesData selectedMovie = addMovies_tableView.getSelectionModel().getSelectedItem();
        
        if (selectedMovie == null) {
            return;
        }
        
        addMovies_movieTitle.setText(selectedMovie.getTitle());
        addMovies_genre.setText(selectedMovie.getGenre());
        addMovies_duration.setText(selectedMovie.getDuration());
        
        if (selectedMovie.getDate() != null) {
            addMovies_date.setValue(selectedMovie.getDate().toLocalDate());
        }
        
        if (selectedMovie.getImage() != null && !selectedMovie.getImage().isEmpty()) {
            try {
                String imagePath = "file:" + selectedMovie.getImage();
                image = new Image(imagePath, 182, 205, false, true);
                addMovies_imageView.setImage(image);
                getData.path = selectedMovie.getImage();
            } catch (Exception e) {
                System.out.println("Không thể load ảnh: " + e.getMessage());
            }
        }
    }
    
    // ===================== AVAILABLE MOVIES (TICKET BOOKING) =====================
    // Load phim đang chiếu
    private void loadAvailableMoviesData() {
        availableMoviesList = FXCollections.observableArrayList();
        
        String sql = "SELECT * FROM movie WHERE current = 'Showing'";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                moviesData movie = new moviesData(
                    rs.getInt("id"),
                    rs.getString("movieTitle"),
                    rs.getString("genre"),
                    rs.getString("duration"),
                    rs.getString("image"),
                    rs.getDate("date"),
                    rs.getString("current")
                );
                availableMoviesList.add(movie);
            }
            
            // Setup table
            availableMovies_col_movieTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            availableMovies_col_genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
            availableMovies_col_showingDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            
            availableMovies_tableView.setItems(availableMoviesList);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể tải danh sách phim: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Select phim từ available movies table
    @FXML
    public void selectAvailableMovies() {
        moviesData selectedMovie = availableMovies_tableView.getSelectionModel().getSelectedItem();
        
        if (selectedMovie == null) {
            return;
        }
        
        availableMovies_movieTitle.setText(selectedMovie.getTitle());
        availableMovies_genre.setText(selectedMovie.getGenre());
        availableMovies_date.setText(String.valueOf(selectedMovie.getDate()));
        
        getData.path = selectedMovie.getImage();
        getData.title = selectedMovie.getTitle();
    }
    
    // Chọn phim để booking
    @FXML
    public void selectMovie() {
        if (availableMovies_movieTitle.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn phim từ danh sách!", AlertType.ERROR);
            return;
        }
        
        // Hiển thị thông tin phim đã chọn
        availableMovies_title.setText(getData.title);
        
        if (getData.path != null && !getData.path.isEmpty()) {
            try {
                String imagePath = "file:" + getData.path;
                image = new Image(imagePath, 182, 226, false, true);
                availableMovies_imageView.setImage(image);
            } catch (Exception e) {
                System.out.println("Không thể load ảnh: " + e.getMessage());
            }
        }
        
        // Clear selection
        clearAvailableMoviesSelection();
    }
    
    @FXML
    public void getSpinnerValue(MouseEvent event) {
        calculateTotal();
    }
    
    // Tính tổng tiền
    private void calculateTotal() {
        qty1 = availableMovies_specialClass_quantity.getValue();
        qty2 = availableMovies_normalClass_quantity.getValue();
        
        price1 = qty1 * SPECIAL_CLASS_PRICE;
        price2 = qty2 * NORMAL_CLASS_PRICE;
        
        total = price1 + price2;
        
        // Update labels
        availableMovies_specialClass_price.setText(formatPrice(price1));
        availableMovies_normalClass_price.setText(formatPrice(price2));
        availableMovies_total.setText(formatPrice(total));
    }
    
    // Mua vé
    @FXML
    public void buy() {
        // Validate
        if (availableMovies_title.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn phim!", AlertType.ERROR);
            return;
        }
        
        calculateTotal();
        
        if (total == 0) {
            showAlert("Lỗi", "Vui lòng chọn số lượng vé!", AlertType.ERROR);
            return;
        }
        
        // Lưu transaction
        String sql = "INSERT INTO customer (type, total, date, time, movieTitle) VALUES(?,?,?,?,?)";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String ticketType = determineTicketType();
            
            pstmt.setString(1, ticketType);
            pstmt.setDouble(2, total);
            pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setTime(4, new java.sql.Time(System.currentTimeMillis()));
            pstmt.setString(5, availableMovies_title.getText());
            
            pstmt.executeUpdate();
            
            // Lấy ID vừa insert
            ResultSet rs = pstmt.getGeneratedKeys();
            int customerId = 0;
            if (rs.next()) {
                customerId = rs.getInt(1);
            }
            
            // Lưu thông tin cho receipt
            saveLastTransaction();
            
            showAlert("Thành công", 
                "Mua vé thành công!\n" +
                "Mã vé: " + customerId + "\n" +
                "Tổng tiền: " + formatPrice(total), 
                AlertType.INFORMATION);
            
            clearAvailableMovies();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể lưu thông tin vé: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Xác định loại vé
    private String determineTicketType() {
        if (qty1 > 0 && qty2 == 0) {
            return "Special Class (" + qty1 + " vé)";
        } else if (qty2 > 0 && qty1 == 0) {
            return "Normal Class (" + qty2 + " vé)";
        } else {
            return "Mixed (" + qty1 + " Special + " + qty2 + " Normal)";
        }
    }
    
    // Lưu thông tin transaction cuối
    private void saveLastTransaction() {
        lastTransaction.movieTitle = availableMovies_title.getText();
        lastTransaction.ticketType = determineTicketType();
        lastTransaction.qty1 = qty1;
        lastTransaction.qty2 = qty2;
        lastTransaction.price1 = price1;
        lastTransaction.price2 = price2;
        lastTransaction.total = total;
        lastTransaction.transactionTime = getCurrentDateTime();
    }
    
    // In hóa đơn
    @FXML
    public void receipt() {
        if (lastTransaction.movieTitle.isEmpty() || lastTransaction.total <= 0) {
            showAlert("Lỗi", "Chưa có giao dịch nào!\nVui lòng mua vé trước.", AlertType.WARNING);
            return;
        }
        
        String receiptContent = generateReceipt();
        
        Alert receiptAlert = new Alert(AlertType.INFORMATION);
        receiptAlert.setTitle("HÓA ĐƠN MUA VÉ");
        receiptAlert.setHeaderText("CINEMA BOOKING SYSTEM");
        receiptAlert.setContentText(receiptContent);
        receiptAlert.getDialogPane().setPrefWidth(400);
        receiptAlert.getDialogPane().setPrefHeight(400);
        receiptAlert.showAndWait();
    }
    
    // Tạo nội dung hóa đơn
    private String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        
        receipt.append("=====================================\n");
        receipt.append("      HÓA ĐƠN MUA VÉ XEM PHIM       \n");
        receipt.append("=====================================\n\n");
        
        receipt.append("THÔNG TIN PHIM:\n");
        receipt.append("Tên phim: ").append(lastTransaction.movieTitle).append("\n");
        receipt.append("Ngày mua: ").append(lastTransaction.transactionTime).append("\n\n");
        
        receipt.append("CHI TIẾT VÉ:\n");
        receipt.append("-------------------------------------\n");
        
        if (lastTransaction.qty1 > 0) {
            receipt.append("Special Class:\n");
            receipt.append("  Số lượng: ").append(lastTransaction.qty1).append(" vé\n");
            receipt.append("  Đơn giá: ").append(formatPrice(SPECIAL_CLASS_PRICE)).append("/vé\n");
            receipt.append("  Thành tiền: ").append(formatPrice(lastTransaction.price1)).append("\n\n");
        }
        
        if (lastTransaction.qty2 > 0) {
            receipt.append("Normal Class:\n");
            receipt.append("  Số lượng: ").append(lastTransaction.qty2).append(" vé\n");
            receipt.append("  Đơn giá: ").append(formatPrice(NORMAL_CLASS_PRICE)).append("/vé\n");
            receipt.append("  Thành tiền: ").append(formatPrice(lastTransaction.price2)).append("\n\n");
        }
        
        receipt.append("=====================================\n");
        receipt.append("TỔNG TIỀN: ").append(formatPrice(lastTransaction.total)).append("\n");
        receipt.append("=====================================\n\n");
        
        receipt.append("Cám ơn quý khách đã sử dụng dịch vụ!\n");
        receipt.append("Chúc quý khách xem phim vui vẻ!\n");
        
        return receipt.toString();
    }
    
    // Clear form available movies
    @FXML
    public void clearAvailableMovies() {
        clearAvailableMoviesSelection();
        availableMovies_title.setText("");
        availableMovies_imageView.setImage(null);
        
        // Reset spinners
        availableMovies_specialClass_quantity.getValueFactory().setValue(0);
        availableMovies_normalClass_quantity.getValueFactory().setValue(0);
        
        // Reset prices
        availableMovies_specialClass_price.setText("0 VND");
        availableMovies_normalClass_price.setText("0 VND");
        availableMovies_total.setText("0 VND");
        
        // Reset variables
        price1 = price2 = total = 0;
        qty1 = qty2 = 0;
    }
    
    private void clearAvailableMoviesSelection() {
        availableMovies_movieTitle.setText("");
        availableMovies_genre.setText("");
        availableMovies_date.setText("");
        availableMovies_tableView.getSelectionModel().clearSelection();
    }
    
    // ===================== EDIT SCREENING =====================
    // Load danh sách phim cho edit screening
    private void loadEditScreeningData() {
        editScreeningList = FXCollections.observableArrayList();
        
        String sql = "SELECT * FROM movie ORDER BY movieTitle ASC";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                // Convert status sang tiếng Việt
                String status = rs.getString("current");
                String vietnameseStatus = convertStatusToVietnamese(status);
                
                moviesData movie = new moviesData(
                    rs.getInt("id"),
                    rs.getString("movieTitle"),
                    rs.getString("genre"),
                    rs.getString("duration"),
                    rs.getString("image"),
                    rs.getDate("date"),
                    vietnameseStatus
                );
                editScreeningList.add(movie);
            }
            
            // Setup table
            editScreening_col_movieTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            editScreening_col_genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
            editScreening_col_duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
            editScreening_col_current.setCellValueFactory(new PropertyValueFactory<>("current"));
            
            // Setup search
            setupEditScreeningSearch();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể tải danh sách phim: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Setup search cho edit screening
    private void setupEditScreeningSearch() {
        FilteredList<moviesData> filteredData = new FilteredList<>(editScreeningList, p -> true);
        
        editScreening_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(movie -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase().trim();
                
                return (movie.getTitle() != null && movie.getTitle().toLowerCase().contains(lowerCaseFilter))
                    || (movie.getGenre() != null && movie.getGenre().toLowerCase().contains(lowerCaseFilter))
                    || (movie.getCurrent() != null && movie.getCurrent().toLowerCase().contains(lowerCaseFilter));
            });
        });
        
        SortedList<moviesData> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(addScreening_tableView.comparatorProperty());
        addScreening_tableView.setItems(sortedData);
    }
    
    // Select phim để edit screening
    @FXML
    public void selectEditScreening() {
        moviesData selectedMovie = addScreening_tableView.getSelectionModel().getSelectedItem();
        
        if (selectedMovie == null) {
            return;
        }
        
        editScreening_title.setText(selectedMovie.getTitle());
        editScreening_current.setValue(selectedMovie.getCurrent());
        
        if (selectedMovie.getImage() != null && !selectedMovie.getImage().isEmpty()) {
            try {
                String imagePath = "file:" + selectedMovie.getImage();
                image = new Image(imagePath, 170, 216, false, true);
                editScreening_imageView.setImage(image);
            } catch (Exception e) {
                System.out.println("Không thể load ảnh: " + e.getMessage());
            }
        }
    }
    
    // Update trạng thái chiếu phim
    @FXML
    public void updateEditScreening() {
        if (editScreening_title.getText().isEmpty() || 
            editScreening_current.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn phim và trạng thái!", AlertType.ERROR);
            return;
        }
        
        String sql = "UPDATE movie SET current = ? WHERE movieTitle = ?";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Convert sang tiếng Anh cho database
            String vietnameseStatus = editScreening_current.getValue();
            String dbStatus = convertStatusToEnglish(vietnameseStatus);
            
            pstmt.setString(1, dbStatus);
            pstmt.setString(2, editScreening_title.getText());
            
            pstmt.executeUpdate();
            
            showAlert("Thành công", "Cập nhật trạng thái thành công!", AlertType.INFORMATION);
            
            clearEditScreening();
            loadEditScreeningData();
            loadAvailableMoviesData(); // Refresh available movies
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể cập nhật: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Clear edit screening form
    @FXML
    public void clearEditScreening() {
        editScreening_title.setText("");
        editScreening_imageView.setImage(null);
        editScreening_current.getSelectionModel().clearSelection();
        addScreening_tableView.getSelectionModel().clearSelection();
    }
    
    // ===================== CUSTOMERS MANAGEMENT =====================
    // Load danh sách khách hàng
    private void loadCustomersData() {
        customersList = FXCollections.observableArrayList();
        
        String sql = "SELECT id, type, total, date, time, movieTitle FROM customer ORDER BY id DESC";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                customerData customer = new customerData(
                    rs.getString("movieTitle"),
                    rs.getString("movieTitle"), // title
                    rs.getInt("id"),
                    rs.getString("type"),
                    0, // quantity không dùng
                    rs.getDouble("total"),
                    rs.getDate("date"),
                    rs.getTime("time")
                );
                customersList.add(customer);
            }
            
            // Setup table
            customers_col_ticketNumber.setCellValueFactory(new PropertyValueFactory<>("id"));
            customers_col_movieTitle.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
            customers_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
            customers_col_time.setCellValueFactory(new PropertyValueFactory<>("time"));
            
            // Setup search
            setupCustomersSearch();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể tải danh sách khách hàng: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Setup search cho customers
    private void setupCustomersSearch() {
        FilteredList<customerData> filteredData = new FilteredList<>(customersList, p -> true);
        
        customers_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase().trim();
                
                return (customer.getMovieTitle() != null && 
                        customer.getMovieTitle().toLowerCase().contains(lowerCaseFilter))
                    || String.valueOf(customer.getId()).contains(lowerCaseFilter)
                    || (customer.getDate() != null && 
                        customer.getDate().toString().contains(lowerCaseFilter));
            });
        });
        
        SortedList<customerData> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(customer_tableView.comparatorProperty());
        customer_tableView.setItems(sortedData);
    }
    
    // Select customer từ table
    @FXML
    public void selectCustomerList() {
        customerData selectedCustomer = customer_tableView.getSelectionModel().getSelectedItem();
        
        if (selectedCustomer == null) {
            return;
        }
        
        customers_ticketNumber.setText(String.valueOf(selectedCustomer.getId()));
        customers_movieTitle.setText(selectedCustomer.getMovieTitle());
        customers_date.setText(selectedCustomer.getDate() != null ? 
            selectedCustomer.getDate().toString() : "");
        customers_time.setText(selectedCustomer.getTime() != null ? 
            selectedCustomer.getTime().toString() : "");
    }
    
    // Xóa customer
    @FXML
    public void deleteCustomer() {
        customerData selectedCustomer = customer_tableView.getSelectionModel().getSelectedItem();
        
        if (selectedCustomer == null) {
            showAlert("Lỗi", "Vui lòng chọn vé cần xóa!", AlertType.ERROR);
            return;
        }
        
        // Confirm xóa
        if (!showConfirmDialog("Xác nhận xóa", 
            "Bạn có chắc chắn muốn xóa vé số: " + selectedCustomer.getId() + "?")) {
            return;
        }
        
        String sql = "DELETE FROM customer WHERE id = ?";
        
        try (Connection conn = database.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, selectedCustomer.getId());
            pstmt.executeUpdate();
            
            showAlert("Thành công", "Xóa vé thành công!", AlertType.INFORMATION);
            
            clearCustomerList();
            loadCustomersData();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Database", "Không thể xóa vé: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    // Clear customer form
    @FXML
    public void clearCustomerList() {
        customers_ticketNumber.setText("");
        customers_movieTitle.setText("");
        customers_date.setText("");
        customers_time.setText("");
        customer_tableView.getSelectionModel().clearSelection();
    }
    
    // ===================== UTILITY METHODS =====================
    // Validate input cho movie
    private boolean validateMovieInput() {
        if (addMovies_movieTitle.getText().trim().isEmpty() ||
            addMovies_genre.getText().trim().isEmpty() ||
            addMovies_duration.getText().trim().isEmpty() ||
            addMovies_date.getValue() == null ||
            addMovies_imageView.getImage() == null) {
            
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!", AlertType.ERROR);
            return false;
        }
        
        // Validate tên phim
        if (!isValidInput(addMovies_movieTitle.getText())) {
            showAlert("Lỗi", "Tên phim chứa ký tự không hợp lệ!", AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    // Check input hợp lệ
    private boolean isValidInput(String input) {
        return input != null && 
               !input.trim().isEmpty() && 
               input.matches("^[a-zA-Z0-9\\s\\-_.,:()]+$");
    }
    
    // Check phim tồn tại
    private boolean isMovieExists(Connection conn, String movieTitle) throws SQLException {
        String sql = "SELECT COUNT(*) FROM movie WHERE movieTitle = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movieTitle);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    // Convert status
    private String convertStatusToVietnamese(String status) {
        switch (status) {
            case "Showing":
                return "Bắt đầu chiếu";
            case "End Showing":
                return "Ngừng chiếu";
            default:
                return status;
        }
    }
    
    private String convertStatusToEnglish(String vietnameseStatus) {
        switch (vietnameseStatus) {
            case "Bắt đầu chiếu":
                return "Showing";
            case "Ngừng chiếu":
                return "End Showing";
            default:
                return vietnameseStatus;
        }
    }
    
    // Format giá tiền
    private String formatPrice(float price) {
        if (price == 0) {
            return "0 VND";
        }
        return String.format("%,.0f VND", price);
    }
    
    // Get current date time
    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return now.format(formatter);
    }
    
    // Show alert dialog
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Show confirm dialog
    private boolean showConfirmDialog(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    // ===================== WINDOW CONTROLS =====================
    // Đóng app
    @FXML
    public void close() {
        System.exit(0);
    }
    
    // Thu nhỏ window
    @FXML
    public void minimize() {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }
    
    // Logout
    @FXML
    public void logout() {
        try {
            // Confirm logout
            if (!showConfirmDialog("Xác nhận", "Bạn có chắc chắn muốn đăng xuất?")) {
                return;
            }
            
            // Hide current window
            signout.getScene().getWindow().hide();
            
            // Load login screen
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            
            // Cho phép kéo window
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            
            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
            });
            
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể logout: " + e.getMessage(), AlertType.ERROR);
        }
    }
}
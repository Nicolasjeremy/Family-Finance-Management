package com.subwayit.gui;

import com.subwayit.dao.PenanggungDAO;
import com.subwayit.dao.TanggunganDAO;
import com.subwayit.model.Penanggung;
import com.subwayit.model.Tanggungan;
import com.subwayit.model.User; // Important as we display both Penanggung and Tanggungan
import com.subwayit.gui.DebtPage; // Add this import
import java.util.List; // Add this line
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image; // For member pictures
import javafx.scene.image.ImageView; // For member pictures
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MembersPage {

    private Stage primaryStage; // Reference to the main stage
    private User loggedInUser; // To hold the user who logged in
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;

    // ObservableList to hold members for display (will be populated from DB)
    private ObservableList<User> familyMembers;
    private GridPane memberCardsGrid; // Reference to the grid to update members

    public MembersPage(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
        this.penanggungDAO = new PenanggungDAO();
        this.tanggunganDAO = new TanggunganDAO();
        this.familyMembers = FXCollections.observableArrayList();
    }

    public Scene createScene() {
        // --- Top Navigation Bar (reused/consistent with Dashboard) ---
        HBox topNav = createTopNavigationBar();

        // --- Main Content Area ---
        VBox mainContent = createMainContentArea();
        ScrollPane scrollPane = new ScrollPane(mainContent); // Make content scrollable
        scrollPane.setFitToWidth(true); // Ensure it takes full width
        scrollPane.setStyle("-fx-background-color: #F0F0F0;"); // Match main content background

        // --- Root Layout ---
        BorderPane root = new BorderPane();
        root.setTop(topNav);
        root.setCenter(scrollPane);
        root.setStyle("-fx-background-color: #F0F0F0;");

        Scene scene = new Scene(root, 1000, 700); // Adjust size as needed
        return scene;
    }

    private HBox createTopNavigationBar() {
        HBox navBar = new HBox(15);
        navBar.setPadding(new Insets(10, 20, 10, 20));
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setStyle("-fx-background-color: #333333;");

        Label logo = new Label("SUBWAYIT");
        logo.setFont(Font.font("Arial", 24));
        logo.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeBtn = createNavLink("Home");
        // Set action for Home button to go back to DashboardPage
        homeBtn.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button dashboardsBtn = createNavLink("Dashboards");
        // Set action for Dashboards button (same as Home for now, or specific dashboard)
        dashboardsBtn.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button membersBtn = createNavLink("Members");
        membersBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"); // Highlight current page
        // Already on members page, so no action needed, or re-render if needed

        Button debtBtn = createNavLink("Debt");
        debtBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;"); // Normal style
        // --- ACTION FOR DEBT BUTTON ---
        debtBtn.setOnAction(e -> {
            DebtPage debtPage = new DebtPage(primaryStage, loggedInUser);
            primaryStage.setScene(debtPage.createScene());
            primaryStage.centerOnScreen();
        });

        navBar.getChildren().addAll(logo, spacer, homeBtn, dashboardsBtn, membersBtn, debtBtn);
        return navBar;
    }

    private Button createNavLink(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        return btn;
    }

    private VBox createMainContentArea() {
        VBox content = new VBox(20); // Spacing between sections
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER); // Align content to top center

        // --- Title and Summary ---
        HBox titleSummary = new HBox(50); // Spacing between title and summary
        titleSummary.setAlignment(Pos.CENTER_LEFT); // Align to left
        titleSummary.setPadding(new Insets(0, 0, 20, 0)); // Padding below this section

        Label membersTitle = new Label("Agus financial members");
        membersTitle.setFont(Font.font("Arial", 24));
        membersTitle.setTextFill(Color.web("#333333"));

        VBox avgStats = new VBox(5);
        avgStats.getChildren().addAll(
                new Label("Average monthly earns"),
                new Label("$25.020,07") // Placeholder
        );
        VBox avgSpending = new VBox(5);
        avgSpending.getChildren().addAll(
                new Label("Average monthly spending"),
                new Label("$22.140,07") // Placeholder
        );

        titleSummary.getChildren().addAll(membersTitle, new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }}, avgStats, avgSpending); // Spacer to push stats right

        // --- Member Cards Grid ---
        memberCardsGrid = new GridPane();
        memberCardsGrid.setHgap(20); // Horizontal gap between cards
        memberCardsGrid.setVgap(20); // Vertical gap between rows

        // Load and display members
        loadAndDisplayMembers();

        content.getChildren().addAll(titleSummary, memberCardsGrid);

        return content;
    }

    /**
     * Loads members from the database and updates the Grid Pane.
     */
    private void loadAndDisplayMembers() {
        memberCardsGrid.getChildren().clear(); // Clear existing cards
        familyMembers.clear(); // Clear observable list

        // --- Load Penanggung (if logged-in user is Penanggung) ---
        if (loggedInUser instanceof Penanggung) {
            Penanggung penanggung = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
            if (penanggung != null) {
                familyMembers.add(penanggung);
            }
        } else { // If a Tanggungan is logged in, only show themselves for now
            Tanggungan tanggungan = tanggunganDAO.getTanggunganById(loggedInUser.getUserId());
            if (tanggungan != null) {
                familyMembers.add(tanggungan);
            }
        }
        // TODO: In a real app, you'd fetch all members of the *logged-in Penanggung's* family
        // This would involve looking up Keluarga by Penanggung ID, then Anggota_Keluarga.
        // For now, this just adds the loggedInUser.

        // To populate with sample dependents regardless of loggedInUser for display purposes:
        // For a real app, ensure these are actual Tanggungan objects from the DB linked to the current Penanggung's family
        List<Tanggungan> allDependents = tanggunganDAO.getAllTanggunan(); // Fetch all dependents (simplify for now)
        for(Tanggungan t : allDependents) {
            if (!t.getUserId().equals(loggedInUser.getUserId())) { // Don't add loggedInUser again if they are Tanggungan
                familyMembers.add(t);
            }
        }


        // --- Populate Grid with Member Cards ---
        int col = 0;
        int row = 0;
        for (User member : familyMembers) {
            VBox memberCard = createMemberCard(member);
            memberCardsGrid.add(memberCard, col, row);
            col++;
            if (col == 4) { // 4 cards per row as per design
                col = 0;
                row++;
            }
        }

        // --- Add Member Card (the '+' button) ---
        VBox addMemberCard = createAddMemberCard();
        memberCardsGrid.add(addMemberCard, col, row);
    }

    /**
     * Creates a visual card for a family member.
     * @param member The User (Penanggung or Tanggungan) object to display.
     * @return A VBox representing the member's card.
     */
    private VBox createMemberCard(User member) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefSize(180, 180); // Fixed size for consistent cards
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Optional: Member Picture (Placeholder for now)
        ImageView profilePic = new ImageView();
        // You can load a default image or user-specific image here if available
        // Example: profilePic.setImage(new Image("file:path/to/default_profile.png"));
        // For now, a blank square or specific icon
        profilePic.setFitWidth(60);
        profilePic.setFitHeight(60);
        profilePic.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 30;"); // Grey circle placeholder

        // Name and Age (e.g., "Agus, 20" or "Dumbo, 12")
        Label nameAgeLabel = new Label(member.getNama() + ", " + member.getUmur());
        nameAgeLabel.setFont(Font.font("Arial", 16));
        nameAgeLabel.setTextFill(Color.web("#333333"));

        // Financial Figure (Placeholder for now, e.g., "$20.000,42")
        Label financialLabel = new Label("$XX,XXX.XX"); // Placeholder
        financialLabel.setFont(Font.font("Arial", 14));
        financialLabel.setTextFill(Color.web("#4CAF50")); // Green color

        // Role and Occupation/Education
        Label roleOccupationLabel = new Label(member.getRole() + "\n" +
                (member instanceof Penanggung ? ((Penanggung) member).getNama() + " Direktur pertamina" : // Placeholder for actual occupation
                 member instanceof Tanggungan ? ((Tanggungan) member).getPendidikan() : "N/A")); // Use education for Tanggungan for now
        roleOccupationLabel.setFont(Font.font("Arial", 12));
        roleOccupationLabel.setTextFill(Color.GRAY);
        roleOccupationLabel.setWrapText(true); // Allow text wrapping

        // Ensure profilePic is centered horizontally relative to other labels
        HBox picContainer = new HBox(profilePic);
        picContainer.setAlignment(Pos.CENTER);
        picContainer.setPadding(new Insets(0,0,10,0)); // Padding below picture

        card.getChildren().addAll(picContainer, nameAgeLabel, financialLabel, roleOccupationLabel);
        return card;
    }

    /**
     * Creates the special card with a "+" button to add new members.
     * @return A VBox representing the "Add Member" card.
     */
    private VBox createAddMemberCard() {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefSize(180, 180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-border-color: #BBBBBB; -fx-border-style: dashed; -fx-border-width: 2;");

        Label plusIcon = new Label("+");
        plusIcon.setFont(Font.font("Arial", 60));
        plusIcon.setTextFill(Color.web("#BBBBBB"));

        card.getChildren().add(plusIcon);

        // Action to open AddMemberForm
        card.setOnMouseClicked(e -> {
            AddMemberForm addMemberForm = new AddMemberForm();
            addMemberForm.display();
            loadAndDisplayMembers(); // Refresh members after adding
        });

        return card;
    }
}
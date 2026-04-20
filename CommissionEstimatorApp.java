import java.time.LocalDateTime;
import java.util.*;


public class CommissionEstimatorApp {

    public static void main(String[] args) {
        //example usage
        Artist artist = new Artist("Jane Doe", 40.0, ExperienceLevel.EXPERIENCED);
        CommissionRules rules = new CommissionRules(
                6, 6,   // min width, min height
                48, 36, // max width, max height
                EnumSet.of(Medium.ACRYLIC, Medium.OIL, Medium.WATERCOLOR),
                EnumSet.of(Subject.PORTRAIT, Subject.LANDSCAPE, Subject.STILL_LIFE, Subject.ABSTRACT),
                EnumSet.of(Frame.NONE, Frame.PLASTIC, Frame.WOOD),
                Complexity.COMPLEX
        );
        artist.setDefaultRules(rules);

        ArtPiece art = new ArtPiece("Portrait", 24, 18, Medium.OIL, Subject.PORTRAIT, Complexity.MODERATE, Frame.WOOD, "Include pet cat");
        Commissioner commissioner = new Commissioner("John Smith", "john@example.com", "555-123-4567");

        CommissionEstimate estimate = new CommissionEstimate(art, artist, commissioner);
        try {
            double price = estimate.calculateEstimate(rules);
            System.out.println("Estimate calculated: $" + String.format("%.2f", price));
            System.out.println(estimate);
        } catch (IllegalArgumentException ex) {
            System.err.println("Estimate failed: " + ex.getMessage());
        }
    }
}

    //enums

enum Medium { ACRYLIC, OIL, WATERCOLOR, DIGITAL }
enum Subject { PORTRAIT, LANDSCAPE, STILL_LIFE, ABSTRACT }
enum Complexity { SIMPLE, MODERATE, COMPLEX }
enum Frame { NONE, PLASTIC, WOOD, CUSTOM }
enum ExperienceLevel { BEGINNER, EXPERIENCED, EXPERT }

    //ArtPiece

class ArtPiece {
    private final UUID id;
    private String title;
    private double widthInInches;
    private double heightInInches;
    private Medium medium;
    private Subject subject;
    private Complexity complexity;
    private Frame frame;
    private String notes;

    public ArtPiece(String title, double widthInInches, double heightInInches,
                    Medium medium, Subject subject, Complexity complexity, Frame frame, String notes) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.widthInInches = widthInInches;
        this.heightInInches = heightInInches;
        this.medium = medium;
        this.subject = subject;
        this.complexity = complexity;
        this.frame = frame;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getWidthInInches() { return widthInInches; }
    public void setWidthInInches(double widthInInches) { this.widthInInches = widthInInches; }

    public double getHeightInInches() { return heightInInches; }
    public void setHeightInInches(double heightInInches) { this.heightInInches = heightInInches; }

    public double getAreaSquareInches() { return widthInInches * heightInInches; }

    public Medium getMedium() { return medium; }
    public void setMedium(Medium medium) { this.medium = medium; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Complexity getComplexity() { return complexity; }
    public void setComplexity(Complexity complexity) { this.complexity = complexity; }

    public Frame getFrame() { return frame; }
    public void setFrame(Frame frame) { this.frame = frame; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

 
        //Validate rules
    public boolean isValid(CommissionRules rules) {
        return rules.validateArtPiece(this);
    }

    @Override
    public String toString() {
        return String.format("ArtPiece[id=%s, title=%s, size=%.1fx%.1f in, medium=%s, subject=%s, complexity=%s, frame=%s, notes=%s]",
                id, title, widthInInches, heightInInches, medium, subject, complexity, frame, notes);
    }
}

    //CommissionEstimate

class CommissionEstimate {
    private final UUID id;
    private final ArtPiece artPiece;
    private final Artist artist;
    private final Commissioner commissioner;
    private double materialCost;
    private double estimatedHours;
    private double priceEstimation;
    private final LocalDateTime createdAt;

    public CommissionEstimate(ArtPiece artPiece, Artist artist, Commissioner commissioner) {
        this.id = UUID.randomUUID();
        this.artPiece = artPiece;
        this.artist = artist;
        this.commissioner = commissioner;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public ArtPiece getArtPiece() { return artPiece; }
    public Artist getArtist() { return artist; }
    public Commissioner getCommissioner() { return commissioner; }
    public double getMaterialCost() { return materialCost; }
    public double getEstimatedHours() { return estimatedHours; }
    public double getPriceEstimation() { return priceEstimation; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    //main workflow to calculate estimate, throws IllegalArgumentException if art piece invalid

    public double calculateEstimate(CommissionRules rules) {
        if (!rules.validateArtPiece(artPiece)) {
            throw new IllegalArgumentException("ArtPiece does not meet commission rules.");
        }
        this.materialCost = calculateMaterialCost(rules);
        this.estimatedHours = estimateTime();
        double laborCost = artist.getHourlyRate() * estimatedHours * artist.getExperienceMultiplier();
        double framingCost = frameCost(artPiece.getFrame());
        double fixedFee = 10.0;
        this.priceEstimation = materialCost + laborCost + framingCost + fixedFee;
        return priceEstimation;
    }

    //material cost based on canvas size and medium, with a small surcharge for larger pieces

    public double calculateMaterialCost(CommissionRules rules) {
        double area = artPiece.getAreaSquareInches();
        double perSqInch;
        switch (artPiece.getMedium()) {
            case OIL: perSqInch = 0.12; break;
            case ACRYLIC: perSqInch = 0.10; break;
            case WATERCOLOR: perSqInch = 0.08; break;
            case DIGITAL: perSqInch = 0.02; break;
            default: perSqInch = 0.10; break;
        }
        //small surcharge for larger pieces
        double sizeMultiplier = Math.min(2.0, 1.0 + (area / 1000.0));
        return Math.round(area * perSqInch * sizeMultiplier * 100.0) / 100.0;
    }

    //Estimate time in hours based on complexity and area

    public double estimateTime() {
        double area = artPiece.getAreaSquareInches();
        double baseHours;
        switch (artPiece.getComplexity()) {
            case SIMPLE: baseHours = 1.0; break;
            case MODERATE: baseHours = 3.0; break;
            case COMPLEX: baseHours = 6.0; break;
            default: baseHours = 3.0; break;
        }
        //scale with area (every 100 sq in adds 1 hour)
        double areaFactor = Math.max(1.0, area / 100.0);
        return Math.round(baseHours * areaFactor * 10.0) / 10.0;
    }

    private double frameCost(Frame frame) {
        switch (frame) {
            case NONE: return 0.0;
            case PLASTIC: return 15.0;
            case WOOD: return 40.0;
            case CUSTOM: return 100.0;
            default: return 0.0;
        }
    }

    @Override
    public String toString() {
        return String.format("CommissionEstimate[id=%s, art=%s, artist=%s, commissioner=%s, materialCost=%.2f, hours=%.1f, price=%.2f, created=%s]",
                id, artPiece.getTitle(), artist.getName(), commissioner != null ? commissioner.getName() : "Anonymous",
                materialCost, estimatedHours, priceEstimation, createdAt);
    }
}

    //Artist

class Artist {
    private final UUID id;
    private String name;
    private double hourlyRate;
    private ExperienceLevel experienceLevel;
    private CommissionRules defaultRules;

    public Artist(String name, double hourlyRate, ExperienceLevel experienceLevel) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.hourlyRate = hourlyRate;
        this.experienceLevel = experienceLevel;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public ExperienceLevel getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(ExperienceLevel experienceLevel) { this.experienceLevel = experienceLevel; }

    public CommissionRules getDefaultRules() { return defaultRules; }
    public void setDefaultRules(CommissionRules defaultRules) { this.defaultRules = defaultRules; }

    
    //returns multiplier based on experience

    public double getExperienceMultiplier() {
        switch (experienceLevel) {
            case BEGINNER: return 0.85;
            case EXPERIENCED: return 1.0;
            case EXPERT: return 1.25;
            default: return 1.0;
        }
    }

    @Override
    public String toString() {
        return String.format("Artist[id=%s, name=%s, hourlyRate=%.2f, experience=%s]", id, name, hourlyRate, experienceLevel);
    }
}

    //Commissioner

class Commissioner {
    private final UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private final List<ArtPiece> savedRequests = new ArrayList<>();

    public Commissioner(String name, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public List<ArtPiece> getSavedRequests() { return Collections.unmodifiableList(savedRequests); }

    public void addRequest(ArtPiece art) {
        if (art != null) savedRequests.add(art);
    }

    public ArtPiece getLatestRequest() {
        if (savedRequests.isEmpty()) return null;
        return savedRequests.get(savedRequests.size() - 1);
    }


     //contact validation

    public boolean validateContact() {
        boolean emailOk = email != null && email.contains("@") && email.contains(".");
        boolean phoneOk = phoneNumber != null && phoneNumber.matches("[0-9\\-\\s\\(\\)]+");
        return emailOk || phoneOk;
    }

    @Override
    public String toString() {
        return String.format("Commissioner[id=%s, name=%s, email=%s, phone=%s, requests=%d]",
                id, name, email, phoneNumber, savedRequests.size());
    }
}

   //CommissionRules

class CommissionRules {
    private double minWidthInInches;
    private double minHeightInInches;
    private double maxWidthInInches;
    private double maxHeightInInches;
    private final Set<Medium> allowedMediums;
    private final Set<Subject> allowedSubjects;
    private final Set<Frame> allowedFrames;
    private Complexity maxComplexity;

    public CommissionRules(double minWidthInInches, double minHeightInInches,
                           double maxWidthInInches, double maxHeightInInches,
                           Set<Medium> allowedMediums, Set<Subject> allowedSubjects,
                           Set<Frame> allowedFrames, Complexity maxComplexity) {
        this.minWidthInInches = minWidthInInches;
        this.minHeightInInches = minHeightInInches;
        this.maxWidthInInches = maxWidthInInches;
        this.maxHeightInInches = maxHeightInInches;
        this.allowedMediums = new HashSet<>(allowedMediums);
        this.allowedSubjects = new HashSet<>(allowedSubjects);
        this.allowedFrames = new HashSet<>(allowedFrames);
        this.maxComplexity = maxComplexity;
    }

    public double getMinWidthInInches() { return minWidthInInches; }
    public double getMinHeightInInches() { return minHeightInInches; }
    public double getMaxWidthInInches() { return maxWidthInInches; }
    public double getMaxHeightInInches() { return maxHeightInInches; }

    public boolean isSizeAllowed(double width, double height) {
        return width >= minWidthInInches && height >= minHeightInInches
                && width <= maxWidthInInches && height <= maxHeightInInches;
    }

    public boolean isMediumAllowed(Medium medium) {
        return allowedMediums.contains(medium);
    }

    public boolean isSubjectAllowed(Subject subject) {
        return allowedSubjects.contains(subject);
    }

    public boolean isFrameAllowed(Frame frame) {
        return allowedFrames.contains(frame);
    }

    public boolean isComplexityAllowed(Complexity complexity) {
        // allow if complexity is less than or equal to maxComplexity
        return complexity.ordinal() <= maxComplexity.ordinal();
    }

    //validation for ArtPiece

    public boolean validateArtPiece(ArtPiece art) {
        if (art == null) return false;
        if (!isSizeAllowed(art.getWidthInInches(), art.getHeightInInches())) return false;
        if (!isMediumAllowed(art.getMedium())) return false;
        if (!isSubjectAllowed(art.getSubject())) return false;
        if (!isFrameAllowed(art.getFrame())) return false;
        if (!isComplexityAllowed(art.getComplexity())) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("CommissionRules[min=%.1fx%.1f, max=%.1fx%.1f, mediums=%s, subjects=%s, frames=%s, maxComplexity=%s]",
                minWidthInInches, minHeightInInches, maxWidthInInches, maxHeightInInches,
                allowedMediums, allowedSubjects, allowedFrames, maxComplexity);
    }
}

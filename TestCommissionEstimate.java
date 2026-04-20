import java.util.EnumSet;

public class TestCommissionEstimate {
    public static void main(String[] args) {
        Artist artist = new Artist("Jane Doe", 40.0, ExperienceLevel.EXPERIENCED);

        CommissionRules rules = new CommissionRules(
                6, 6,
                48, 36,
                EnumSet.of(Medium.ACRYLIC, Medium.OIL),
                EnumSet.of(Subject.PORTRAIT, Subject.LANDSCAPE),
                EnumSet.of(Frame.NONE, Frame.WOOD),
                Complexity.COMPLEX
        );

        ArtPiece art = new ArtPiece(
                "Portrait",
                24, 18,
                Medium.OIL,
                Subject.PORTRAIT,
                Complexity.MODERATE,
                Frame.WOOD,
                "Include pet"
        );

        Commissioner commissioner = new Commissioner("John Smith", "john@example.com", "555-123-4567");

        CommissionEstimate estimate = new CommissionEstimate(art, artist, commissioner);

        double price = estimate.calculateEstimate(rules);

        System.out.println("Estimate price: $" + price);
        System.out.println(estimate);
    }
}

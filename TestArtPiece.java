public class TestArtPiece {
    public static void main(String[] args) {
        ArtPiece art = new ArtPiece(
                "Test piece",
                12, 16,
                Medium.ACRYLIC,
                Subject.LANDSCAPE,
                Complexity.MODERATE,
                Frame.WOOD,
                "Test notes"
        );

        System.out.println("Created ArtPiece:");
        System.out.println(art);

        art.setTitle("Updated Title");
        art.setWidthInInches(20);
        art.setHeightInInches(10);

        System.out.println("\nAfter updates:");
        System.out.println("Title: " + art.getTitle());
        System.out.println("Area: " + art.getAreaSquareInches());
    }
}

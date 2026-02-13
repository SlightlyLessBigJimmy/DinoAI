import java.util.ArrayList;

public class Raycast {
    public boolean Hit = false;
    public Vector2 Position = null;
    public Vector2 Normal = null;
    public Sprite Instance = null;

    public Raycast(Vector2 origin, Vector2 direction, double maxDistance, ArrayList<Sprite> ignorelist) {

        ArrayList<Sprite> sprites = Main.sprites;

        // Normalize direction
        double length = Math.sqrt(direction.x * direction.x + direction.y * direction.y);
        if (length == 0) return;

        direction = new Vector2(direction.x / length, direction.y / length);

        double closestDistance = maxDistance;

        for (Sprite sprite : sprites) {

            if (!sprite.getCanCollide()) continue;
            if (ignorelist != null && ignorelist.contains(sprite)) continue;

            // AABB bounds
            double minX = sprite.Position.x - sprite.GetSize().x / 2;
            double maxX = sprite.Position.x + sprite.GetSize().x / 2;
            double minY = sprite.Position.y - sprite.GetSize().y / 2;
            double maxY = sprite.Position.y + sprite.GetSize().y / 2;

            // Ray vs AABB (slab method)
            double t1 = (minX - origin.x) / direction.x;
            double t2 = (maxX - origin.x) / direction.x;
            double t3 = (minY - origin.y) / direction.y;
            double t4 = (maxY - origin.y) / direction.y;

            double tmin = Math.max(Math.min(t1, t2), Math.min(t3, t4));
            double tmax = Math.min(Math.max(t1, t2), Math.max(t3, t4));

            if (tmax < 0) continue;          // Box is behind ray
            if (tmin > tmax) continue;       // No intersection
            if (tmin < 0) continue;          // Ray starts inside box (optional: remove if you want inside hits)

            if (tmin <= closestDistance) {
                closestDistance = tmin;

                Hit = true;
                Instance = sprite;
                Position = new Vector2(
                        origin.x + direction.x * tmin,
                        origin.y + direction.y * tmin
                );

                // Determine normal
                if (tmin == t1) Normal = new Vector2(-1, 0);
                else if (tmin == t2) Normal = new Vector2(1, 0);
                else if (tmin == t3) Normal = new Vector2(0, -1);
                else Normal = new Vector2(0, 1);
            }
        }
    }
}

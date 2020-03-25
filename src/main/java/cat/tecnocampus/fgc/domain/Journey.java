package cat.tecnocampus.fgc.domain;

import javax.validation.constraints.NotNull;

public class Journey {

    private long id;

    @NotNull
    private Station origin;

    @NotNull
    private Station destination;

    public Journey() {
        origin = new Station();
        destination = new Station();
    }

    public Journey(Station origin, Station destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Station getOrigin() {
        return origin;
    }

    public void setOrigin(Station origin) {
        this.origin = origin;
    }

    public Station getDestination() {
        return destination;
    }

    public void setDestination(Station destination) {
        this.destination = destination;
    }

    public boolean sameOriginDestination() {
        return origin.getNom().equalsIgnoreCase(destination.getNom());
    }

    @Override
    public String toString() {
        return "Journey{" +
                "id=" + id +
                ", origin=" + origin +
                ", destination=" + destination +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Journey journey = (Journey) o;

        if (origin != null ? !origin.equals(journey.origin) : journey.origin != null) return false;
        return destination != null ? destination.equals(journey.destination) : journey.destination == null;
    }

    @Override
    public int hashCode() {
        int result = origin != null ? origin.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }
}

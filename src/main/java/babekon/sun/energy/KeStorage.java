package babekon.sun.energy;

public interface KeStorage {
    int insertKe(int amount, boolean simulate);

    int extractKe(int amount, boolean simulate);

    int getKeStored();

    int getKeCapacity();
}

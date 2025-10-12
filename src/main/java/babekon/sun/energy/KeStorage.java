package babekon.sun.energy;

/**
 * Simple KE storage/transfer interface for blocks and cables.
 */
public interface KeStorage {
    /**
     * Try to insert KE units into this storage.
     * @param amount requested amount to insert
     * @param simulate if true, do not modify state
     * @return actually accepted amount
     */
    int insertKe(int amount, boolean simulate);

    /**
     * Try to extract KE units from this storage.
     * @param amount requested amount to extract
     * @param simulate if true, do not modify state
     * @return actually extracted amount
     */
    int extractKe(int amount, boolean simulate);

    int getKeStored();

    int getKeCapacity();
}

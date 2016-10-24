package xuin.advance.algorithmics.file.system;

public class ClubManagement {
    private static final String EMPTY_POSITION = "-1";
    private static final char DELETE_INDICATOR = '?';
    private static final int START_CONTENT = 2;
    private static final int BLOCK = 3;

    // assume that there is less than 100 clubs in the file system.
    // assume that each club will be stored as 3 chars.
    // this data represent for a file system.
    // - it starts with 2 chars which indicated where latest deleted position.
    // - each 3 chars follows is the content of club.
    // if content starts with ? that mean this record was being deleted, and
    // 2 chars follow indicated that the next deleted record position was stored.
    // otherwise it will contains 3 chars of club name.
    private StringBuilder data = new StringBuilder(EMPTY_POSITION);

    public void add(String club) {
        if (find(club) == -1) {
            int root = root();
            if (root == -1) {
                // in case don't have any deleted record yet, append new record into the end.
                data.append(club);
            } else {
                // the number which store to indicate the delete position is index,
                // transform index to real position in file system
                int position = START_CONTENT + root * 3;
                // link root to next delete record.
                data.replace(0, 2, data.substring(position + 1, position + BLOCK));
                // overwrite the data to delete record.
                data.replace(position, position + BLOCK, club);
            }
        }
    }

    public void delete(String club) {
        int record = find(club);
        if (record != -1) {
            // transform index to position.
            int position = START_CONTENT + record * 3;
            // update root point to new deleted record.
            data.replace(position, position + BLOCK, DELETE_INDICATOR + data.substring(0, 2));
            // keep the link to next delete record.
            data.replace(0, 2, String.format("%02d", record));
        }
    }

    public void defragment() {
        // reset root to null.
        data.replace(0, 2, EMPTY_POSITION);
        for (int i = START_CONTENT; i < data.length(); i = i + BLOCK) {
            if (DELETE_INDICATOR == data.charAt(i)) {
                // this step in this sample will run very fast, because it on memory
                // in fact, for the file system this step take very long time to shift
                // memory to the new position.
                data.replace(i, i + BLOCK, "");
                // after remove 3 chars, reset the counter.
                i = i - 3;
            }
        }
    }

    private int find(String club) {
        for (int i = START_CONTENT; i < data.length(); i = i + BLOCK) {
            if (DELETE_INDICATOR != data.charAt(i)) {
                if (data.subSequence(i, i + BLOCK).equals(club)) {
                    // return the index of block instead of the position
                    // because we surely able to calculate this position again
                    // when we need to used it.
                    // keeping the index will save more memory, in case of fixed
                    // length then it more efficiency.
                    return (i - START_CONTENT) / BLOCK;
                }
            }
        }
        // in case doesn't found record.
        return -1;
    }

    private int root() {
        return Integer.parseInt(data.subSequence(0, START_CONTENT).toString());
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
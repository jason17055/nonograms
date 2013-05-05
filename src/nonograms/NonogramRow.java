package nonograms;

public interface NonogramRow
{
	int getHintCount();
	int getHint(int hintIdx);
	int getLength();
	byte get(int index);
	void set(int index, byte value);
}

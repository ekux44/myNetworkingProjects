public class RUDP {

	public String getFilename() {
		return filename;
	}

	public int getMode() {
		switch (mode) {
		case StopAndWait:
			return 0;
		case SlidingWindow:
			return 1;
		}
		return -1;
	}

	public long getModeParameter() {
		return slidingWindowSize;
	}

	public void setFilename(String fName) {
		filename = fName;
	}

	public boolean setMode(int modeNum) {
		switch (modeNum) {
		case 0:
			mode = Mode.StopAndWait;
			slidingWindowSize = 1;
			return true;
		case 1:
			mode = Mode.SlidingWindow;
			slidingWindowSize = 256;
			return true;
		default:
			return false;
		}
	}

	public boolean setModeParameter(long param) {
		if (mode == Mode.SlidingWindow) {
			slidingWindowSize = param;
			return true;
		}
		return false;
	}

	Mode mode = Mode.StopAndWait;
	long slidingWindowSize = 0;
	String filename;
	
	public enum Mode{
		StopAndWait, SlidingWindow
	}
}

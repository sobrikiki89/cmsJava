package app.core.crypto;

import java.io.Serializable;

/**
 * <p>
 * Convenience class to deal with 8 byte PIN block format. It provides
 * conversion method to long, byte array, and hexadecimal string.
 * </p>
 */
public final class PinBlock implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long block;

	public PinBlock(final long block) {
		super();
		this.block = block;
	}

	public PinBlock(final byte[] buf) {
		super();
		this.block = ((long) buf[0] & 0xFF) << 56 | ((long) buf[1] & 0xFF) << 48 | ((long) buf[2] & 0xFF) << 40
				| ((long) buf[3] & 0xFF) << 32 | ((long) buf[4] & 0xFF) << 24 | ((long) buf[5] & 0xFF) << 16
				| ((long) buf[6] & 0xFF) << 8 | ((long) buf[7] & 0xFF);
	}

	public PinBlock(final String hex) {
		super();
		long l = 0;

		for (int i = 0; i < 8; i++) {
			final String b = hex.substring(2 * i, 2 * i + 2); // one byte
			l = l | Long.parseLong(b, 16) << (56 - i * 8);
		}

		this.block = l;
	}

	public PinBlock xor(final PinBlock other) {
		return new PinBlock(this.block ^ other.block);
	}

	public long toLong() {
		return block;
	}

	public byte[] toByteArray() {
		final byte[] buf = new byte[8];

		buf[0] = (byte) ((block >>> 56) & 0xFFL);
		buf[1] = (byte) ((block >>> 48) & 0xFFL);
		buf[2] = (byte) ((block >>> 40) & 0xFFL);
		buf[3] = (byte) ((block >>> 32) & 0xFFL);
		buf[4] = (byte) ((block >>> 24) & 0xFFL);
		buf[5] = (byte) ((block >>> 16) & 0xFFL);
		buf[6] = (byte) ((block >>> 8) & 0xFFL);
		buf[7] = (byte) (block & 0xFFL);

		return buf;
	}

	private static final String hexmap = "0123456789ABCDEF";

	public String toHexString() {
		final StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 16; i++) {
			final int nibble = (int) ((block >>> (60 - 4 * i)) & 0x0FL);
			sb.append(hexmap.charAt(nibble));
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 16; i++) {
			if ((i > 0) && (i % 4 == 0)) {
				sb.append(' ');
			}
			final int nibble = (int) ((block >>> (60 - 4 * i)) & 0x0FL);
			sb.append(hexmap.charAt(nibble));
		}

		return sb.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		final PinBlock o2 = (PinBlock) obj;
		return this.block == o2.block;
	}

	@Override
	public int hashCode() {
		return (int) (block ^ (block >>> 32));
	}

}

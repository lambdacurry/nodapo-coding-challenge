package de.testaufgaben.nodapo;

public enum Genre {
	ADV,
	BIO,
	COM,
	FAN;
	
	@Override
    public String toString(){
        switch (this) {
            case ADV: return "Adventure";
            case BIO: return "Biography";
            case COM: return "Comic";
            case FAN: return "Fantasy";
            default: return "undefined genre";
        }
    }
}

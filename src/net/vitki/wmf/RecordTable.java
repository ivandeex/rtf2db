package net.vitki.wmf;

import java.util.Vector;

import net.vitki.wmf.emeta.EmfHeader;
import net.vitki.wmf.wmeta.WmfHeader;

public class RecordTable {

    private WmfHeader wmf_header;
    private EmfHeader emf_header;
    private ApmHeader apm_header;

    private Vector recs = null;

    public RecordTable() {
        wmf_header = null;
        emf_header = null;
        apm_header = null;
        recs = new Vector();
    }

    public boolean     isPlaceable()    { return (apm_header != null); }
    public boolean     isEnhanced()     { return (emf_header != null); }

    public WmfHeader  getWmfHeader()   { return wmf_header; }
    public EmfHeader  getEmfHeader()   { return emf_header; }
    public ApmHeader  getApmHeader()   { return apm_header; }

    public boolean add (Record rec)  {
        if (wmf_header == null && emf_header == null)  {
            System.err.println ("cannot add records when no header is present");
            return false;
        }
        recs.add(rec);
        return true;
    }

    public boolean addApmHeader (ApmHeader apm_header)  {
        if (this.wmf_header != null || this.emf_header != null
            || this.apm_header != null)  {
            System.err.println ("cannot add duplicate APM header");
            return false;
        }
        this.apm_header = apm_header;
        recs.add(apm_header);
        return true;
    }

    public boolean addWmfHeader (WmfHeader wmf_header)  {
        if (this.wmf_header != null || this.emf_header != null)  {
            System.err.println ("cannot add duplicate WMF header");
            return false;
        }
        this.wmf_header = wmf_header;
        recs.add (wmf_header);
        return true;
    }

    public boolean addEmfHeader (EmfHeader emf_header)  {
        if (this.emf_header != null || this.wmf_header != null)  {
            System.err.println ("cannot add duplicate EMF header");
            return false;
        }
        this.emf_header = emf_header;
        recs.add(emf_header);
        return true;
    }

    public int getSize() {
        return recs.size();
    }

    public Record get(int i) {
        return (Record)recs.get(i);
    }

}


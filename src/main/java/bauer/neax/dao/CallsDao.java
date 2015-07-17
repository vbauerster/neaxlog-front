package bauer.neax.dao;

import java.util.List;
import java.util.Map;

import bauer.neax.domain.Calls;
import bauer.neax.domain.ExtAcl;
import bauer.neax.domain.PinOwner;
import bauer.neax.domain.ReportParam;

public interface CallsDao {

	public List<String> getActivePins (String alias);

	public List<String> getDistinctExts();

	public int getActivePinsCount (String alias);

	public List<Calls> getReport(ReportParam p);

//	public List<Calls> getCalls(SortedSet<Person> personSet, int line, Date from, Date to);
//
//	public List<Calls> getCallsByExt(List<String> exts, int line, Date from, Date to);

	public boolean isNotifyEnabled(String alias);

	public int setNotify(boolean notify, String alias);

	public boolean insertExtAcl(Map<String, Object> extAcl);

	public List<ExtAcl> getExtAcl(String ext);

	public int delExtAcl(int ext, String alias);

	public List<String> getExtPermissions(String alias);

	public List<String> getUserRoles(String alias);

	public List<String> getAliases(boolean active);

	public List<PinOwner> getPinOwners();

	public List<PinOwner> seekPinOwners(String input);

	public PinOwner getPinOwner(String pin);

	public void updPinOwner(final PinOwner pinOwner);

	public void insPinOwner(PinOwner pinOwner);

	public Map<String,Object> deactivatePin(Map<String, String> argMap);

//	public String getAliasByGin(String gin);
}

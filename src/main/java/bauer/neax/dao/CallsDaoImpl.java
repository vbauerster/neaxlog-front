package bauer.neax.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.transaction.annotation.Transactional;

import bauer.neax.domain.Calls;
import bauer.neax.domain.ExtAcl;
import bauer.neax.domain.Person;
import bauer.neax.domain.PinOwner;
import bauer.neax.domain.ReportParam;

public class CallsDaoImpl implements CallsDao {

	static final private String selActivePins = "select AuthCode from ACODE_OWNERS where Alias=? and Status=1";
	static final private String selPinCount = "select count(*) from ACODE_OWNERS where Alias=? and Status=1";

	static final private String selNotifyBit = "select Notify from OWNER_INFO where Alias=?";

	static final private String updNotifyBit = "update OWNER_INFO set Notify=? where Alias=?";

	static final private String selDistinctExt = "select distinct Extension from CALLS order by Extension";

	static final private String selExtAcl = "select ALIAS, ADDEDBY, EVENTIME from EXT_ACL where EXT=?";

	static final private String delExtAcl = "delete from EXT_ACL where EXT=? and ALIAS=?";

	static final private String selExtPerm = "select EXT from EXT_ACL where ALIAS=?";

	static final private String USER_ROLES = "select Role from user_roles where alias=?";

	static final private String aliases_AO = "select distinct Alias from ACODE_OWNERS where Status=1 order by Alias";

	static final private String aliases_OI = "select Alias from OWNER_INFO order by Alias";

	static final private String CALLS_BY_USERS = "select c.TrunkRoute, c.Extension, c.CalledNumber, c.Duration, c.CallTime, c.AuthCode, oi.FirstName + ' ' + oi.LastName as CN" +
											 " from CALLS c join ACODE_OWNERS ao on (c.AuthCode=ao.AuthCode)" +
											 " left join OWNER_INFO oi on (ao.Alias=oi.Alias)" +
											 " where ao.Alias in () and (c.CallTime between ? and ?)" +
											 " order by ao.Alias, c.CallTime";

	static final private String selCallByExt = "select c.TrunkRoute, c.Extension, c.CalledNumber, c.Duration, c.CallTime, c.AuthCode, oi.FirstName + ' ' + oi.LastName as CN"+
											" from CALLS c left join ACODE_OWNERS ao on (c.AuthCode=ao.AuthCode)" +
											" left join OWNER_INFO oi on (ao.Alias=oi.Alias)" +
											" where c.Extension in () and (c.CallTime between ? and ?)" +
											" order by c.Extension, c.CallTime";

	static final private String pinOwnerSeek = "select oi.Alias, oi.FirstName, oi.LastName, oi.Mail, ao.AuthCode, oi.Notify, ao.EvenTime, ao.UpdatedBy" +
											" from ACODE_OWNERS ao join OWNER_INFO oi on (ao.Alias = oi.Alias)" +
											" where ao.Status=1 and (oi.Alias like '?' or oi.FirstName + ' ' + oi.LastName like '?')";

	static final private String selPinOwners="select * from OWNER_INFO";

//	static final private String selPinOwners="select distinct oi.Alias, oi.FirstName, oi.LastName, oi.Mail, oi.Notify, oi.Alias as AuthCode, ao.EvenTime, ao.UpdatedBy" +
//											" from OWNER_INFO oi LEFT OUTER JOIN ACODE_OWNERS ao ON oi.Alias=ao.Alias" +
//											" where ao.Status=1 order by oi.Alias";

	static final private String updOwnerInfo = "update OWNER_INFO set FirstName=?, LastName=?, Mail=?, Notify=? where Alias=?";

	static final private String updAcodeOwn = "update ACODE_OWNERS set AuthCode=?, UpdatedBy=?, EvenTime=GETDATE() where AuthCode=?";

//	static final private String alias_by_gin = "select alias from OWNER_INFO where EmployeeNumber=?";

	private static final Logger log = LoggerFactory.getLogger(CallsDaoImpl.class);

	private JdbcTemplate jdbcTemplate;
	private SimpleJdbcCall procInsExtAcl;
	private SimpleJdbcCall procInsPinOwner;
	private SimpleJdbcCall procDeactivatePin;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.procInsExtAcl = new SimpleJdbcCall(dataSource).withProcedureName("usp_insExtAcl");
        this.procInsPinOwner = new SimpleJdbcCall(dataSource).withProcedureName("usp_insPinOwner");
        this.procDeactivatePin = new SimpleJdbcCall(dataSource).withProcedureName("usp_deactivatePin");
    }

    public void insPinOwner(PinOwner pinOwner) {

		Map<String, Object> param = new HashMap<String, Object>(7);
		param.put("alias", pinOwner.getAlias());
		param.put("firstName", pinOwner.getFirstName());
		param.put("lastName", pinOwner.getLastName());
		param.put("mail", pinOwner.getMail());
//		param.put("empNum", pinOwner.getEmployeeNumber());
		param.put("notify", pinOwner.isNotify() ? 1 : 0);
		param.put("authCode", pinOwner.getAuthCode());
		param.put("updatedBy", pinOwner.getUpdatedBy());

//    	try{
    		Map<String,Object> out = procInsPinOwner.execute(param);
    		log.debug("Out map returned by usp_insPinOwner: " + out);
//    	}catch(DuplicateKeyException dke){
//    		log.error("Cannot insert: "  + pinOwner);
//    		log.error("Exception: " + dke.getMessage());
//
//    		throw dke;
//    	}
    }


    public Map<String,Object> deactivatePin(Map<String, String> argMap){

    	try{
    		Map<String,Object> out = procDeactivatePin.execute(argMap);
    		log.debug("Out map returned by procDeactivatePin: " + out);
//    		logger.info(out.get("alias"));
    		return out;
    	}catch (Exception e){
    		log.error("Cannot deactivate: " + argMap);
    		log.error("Exception: ", e);
    		return null;
    	}

    }


    public boolean insertExtAcl(Map<String, Object> extAcl){
    	try{
    		Map<String,Object> out = procInsExtAcl.execute(extAcl);
    		log.debug("Out map returned by usp_insExtAcl: " + out);
    		return true;
    	}catch (Exception e){
    		log.error("Cannot insert: " + extAcl);
    		log.error("Exception: " + e.getMessage());
    		return false;
    	}
    }


    public List<ExtAcl> getExtAcl(String ext){

    	return jdbcTemplate.query(selExtAcl,
    	        new RowMapper<ExtAcl>() {
            	public ExtAcl mapRow(ResultSet rs, int rowNum) throws SQLException {
            		return new ExtAcl(rs.getString(1), rs.getString(2), rs.getTimestamp(3));
            	}
        	}, ext);

    }

    public int delExtAcl(int ext, String alias){
    	return jdbcTemplate.update(delExtAcl, ext, alias);
    }

    public boolean isNotifyEnabled(String alias){
    	int status = jdbcTemplate.queryForInt(selNotifyBit, alias);
    	return status == 0 ? false : true;
    }

    /**
     *
     * @param notify
     * @return the number of rows affected
     */
    public int setNotify(boolean notify, String alias){

    	int status = notify ? 1 : 0;

    	return jdbcTemplate.update(updNotifyBit, status, alias);
    }


    @Transactional
    public void updPinOwner(final PinOwner pinOwner){

//    	int status = pinOwner.isNotify() ? 1 : 0;

    	int ucount = jdbcTemplate.update(updOwnerInfo,
    			new PreparedStatementSetter(){

					public void setValues(PreparedStatement ps) throws SQLException {
//						ps.setString(1, pinOwner.getAlias());
						ps.setString(1, pinOwner.getFirstName());
						ps.setString(2, pinOwner.getLastName());
						ps.setString(3, pinOwner.getMail());
//						ps.setString(4, pinOwner.getEmployeeNumber());
						ps.setBoolean(4, pinOwner.isNotify());
						ps.setString(5, pinOwner.getAlias());
					}
    	});
    	log.debug("OWNER_INFO updated: " + ucount);

    	ucount = jdbcTemplate.update(updAcodeOwn,
    			new PreparedStatementSetter(){

					public void setValues(PreparedStatement ps) throws SQLException {

						ps.setString(1, pinOwner.getAuthCode()); // new pin
						ps.setString(2, pinOwner.getUpdatedBy());
						ps.setString(3, pinOwner.getWhAuthCode());// old pin
					}
    	});
    	log.debug("ACODE_OWNERS updated: " + ucount);

    }


    public List<String> getActivePins (String alias){
    	return jdbcTemplate.queryForList(selActivePins, String.class, alias);
    }


    public List<String> getDistinctExts(){
    	return jdbcTemplate.queryForList(selDistinctExt, String.class);
    }


    public List<String> getExtPermissions(String alias){
    	return jdbcTemplate.queryForList(selExtPerm, String.class, alias);
    }


    public List<String> getUserRoles(String alias){
    	return jdbcTemplate.queryForList(USER_ROLES, String.class, alias);
    }


    public List<String> getAliases(boolean active){

    	if(active)
    		return jdbcTemplate.queryForList(aliases_AO, String.class);
    	else
    		return jdbcTemplate.queryForList(aliases_OI, String.class);
    }


    public int getActivePinsCount (String alias){
    	int p = jdbcTemplate.queryForInt(selPinCount, alias);
    	log.debug("{} has {} pin", alias, p);
    	return  p;
    }


    public List<Calls> getReport(ReportParam p){
    	if(p.isByExt())
    		return getCallsByExt(p.getSelectedExts(), p.getLine(), p.getFrom(), p.getTill());
    	else
    		return getCalls(p.getReporters(), p.getLine(), p.getFrom(), p.getTill());
    }


    private List<Calls> getCalls(SortedSet<Person> personSet, int line, Date from, Date to){

    	if(personSet==null) throw new RuntimeException("PersonSet object cannot be null!");

    	StringBuilder sql = new StringBuilder(CALLS_BY_USERS);

    	String alias = null;

    	if(!personSet.isEmpty()){
    		alias = "'" + personSet.first().getAlias() + "'";

    		int i=0;
    		for(Person p : personSet){
    			i++;
    			if(i==1) continue;

    			alias = alias + ", '" + p.getAlias() + "'";

    		}
    		log.debug("Aliases: ({})",alias);
    	}

    	int nIndex = sql.indexOf("()")+1;
    	sql.insert(nIndex, alias);

    	nIndex = sql.indexOf("?)", nIndex) + 2;

//    	String trunk = line==2 ? " AND c.TrunkRoute=5" : (line==3 ? " AND c.TrunkRoute<>5" : "");

    	sql.insert(nIndex, getTrunkLine(line));

    	log.debug(sql.toString());

    	return jdbcTemplate.query(sql.toString(), new CallMapper(), from, to);

    }


    private List<Calls> getCallsByExt(List<String> exts, int line, Date from, Date to){

    	if(exts==null) throw new RuntimeException("Extension cannot be null!");

    	StringBuilder sql = new StringBuilder(selCallByExt);

    	String ext = "";

    	if(!exts.isEmpty()){

    		ext = exts.get(0);

    		for(int i=1; i<exts.size(); i++){

    			ext = ext + ", " + exts.get(i);

    		}
    		log.debug("Selected exts: ({})", ext);
    	}

    	int nIndex = sql.indexOf("()")+1;
    	sql.insert(nIndex, ext);

    	nIndex = sql.indexOf("?)", nIndex) + 2;
    	sql.insert(nIndex, getTrunkLine(line));

    	log.debug(sql.toString());

    	return jdbcTemplate.query(sql.toString(), new CallMapper(), from, to);
    }


    public List<PinOwner> getPinOwners(){

//    	StringBuilder sql = new StringBuilder(pinOwnerSeek);
//    	sql.delete(sql.indexOf("where"), sql.length());
//    	log.debug(sql.toString());

    	return jdbcTemplate.query(selPinOwners, new PinOwnerMapper(true));
    }


    public List<PinOwner> seekPinOwners(String input){

    	StringBuilder sql = new StringBuilder(pinOwnerSeek);

    	int n = 0;
    	do{
    	 n = sql.indexOf("?", n);
    	 if(n!=-1)
    	  sql.replace(n, n+1, input);
    	}while (n > 0);


    	log.debug(sql.toString());

    	return jdbcTemplate.query(sql.toString(), new PinOwnerMapper(false));
    }


//    public String getAliasByGin(String gin){
//
//    	try{
//    		return jdbcTemplate.queryForObject(alias_by_gin, String.class, gin);
//    	}catch(EmptyResultDataAccessException e){
//    		return null;
//    	}
//    }


    public PinOwner getPinOwner(String pin){

    	StringBuilder sql = new StringBuilder(pinOwnerSeek);

    	int n = sql.indexOf("1");
    	sql.replace(n+1, sql.length(), " and ao.AuthCode='" + pin + "'");

    	log.debug(sql.toString());
    	return jdbcTemplate.queryForObject(sql.toString(), new PinOwnerMapper(false));
    }


    private String getTrunkLine(int line){
    	return line==2 ? " AND c.TrunkRoute=5" : (line==3 ? " AND c.TrunkRoute<>5" : "");
    }


    private static final class PinOwnerMapper implements RowMapper<PinOwner>{

    	private boolean oi_only;

    	public PinOwnerMapper(boolean oi_only){
    		this.oi_only = oi_only;
    	}

		public PinOwner mapRow(ResultSet rs, int rowNum) throws SQLException {

			PinOwner pinOwner = new PinOwner();

			pinOwner.setAlias(rs.getString("Alias"));
			pinOwner.setFirstName(rs.getString("FirstName"));
			pinOwner.setLastName(rs.getString("LastName"));
			pinOwner.setMail(rs.getString("Mail"));
			pinOwner.setNotify(rs.getBoolean("Notify"));

			if(!oi_only){
			pinOwner.setAuthCode(rs.getString("AuthCode"));
			pinOwner.setEvenTime(rs.getTimestamp("EvenTime"));
			pinOwner.setUpdatedBy(rs.getString("UpdatedBy"));
			}
			return pinOwner;
		}

    }


    private static final class CallMapper implements RowMapper<Calls>{

		public Calls mapRow(ResultSet rs, int rowNum) throws SQLException {

			Calls call = new Calls();

			call.setCallTime(rs.getTimestamp("CallTime"));
			call.setLine(rs.getInt("TrunkRoute")==5 ? "SINET" : "LANDLINE");
			call.setExt(rs.getInt("Extension"));
			call.setCalledNumber(rs.getString("CalledNumber"));
			call.setDuration(rs.getLong("Duration"));
			call.setAuthCode(rs.getString("AuthCode"));
			call.setCommonName(rs.getString("CN"));

			return call;
		}

    }

}

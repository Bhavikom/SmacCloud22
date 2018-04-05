package de.smac.smaccloud.model;


import com.google.gson.annotations.SerializedName;


public class CreateChannnelModel {

	private boolean isSelected;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	@SerializedName("UserMobileNo")

	private Object userMobileNo;

	@SerializedName("ThemeData")
	private Object themeData;

	@SerializedName("Designation")
	private String designation;

	@SerializedName("IsDeleted")
	private boolean isDeleted;

	@SerializedName("Email")
	private String email;

	@SerializedName("Address")
	private String address;

	@SerializedName("InsertDate")
	private String insertDate;

	@SerializedName("InsertedOn")
	private String insertedOn;

	@SerializedName("DeletedOn")
	private Object deletedOn;

	@SerializedName("RoleId")
	private int roleId;

	@SerializedName("DeleteDate")
	private String deleteDate;

	@SerializedName("Name")
	private String name;

	@SerializedName("Contact")
	private String contact;

	@SerializedName("UpdateDate")
	private String updateDate;

	@SerializedName("Org_Id")
	private int orgId;

	@SerializedName("AccessToken")
	private Object accessToken;

	@SerializedName("SyncType")
	private int syncType;

	@SerializedName("UpdatedOn")
	private Object updatedOn;

	@SerializedName("AuthToken")
	private Object authToken;

	@SerializedName("Id")
	private int id;

	@SerializedName("UserLanguage")
	private String userLanguage;

	public void setUserMobileNo(Object userMobileNo){
		this.userMobileNo = userMobileNo;
	}

	public Object getUserMobileNo(){
		return userMobileNo;
	}

	public void setThemeData(Object themeData){
		this.themeData = themeData;
	}

	public Object getThemeData(){
		return themeData;
	}

	public void setDesignation(String designation){
		this.designation = designation;
	}

	public String getDesignation(){
		return designation;
	}

	public void setIsDeleted(boolean isDeleted){
		this.isDeleted = isDeleted;
	}

	public boolean isIsDeleted(){
		return isDeleted;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setInsertDate(String insertDate){
		this.insertDate = insertDate;
	}

	public String getInsertDate(){
		return insertDate;
	}

	public void setInsertedOn(String insertedOn){
		this.insertedOn = insertedOn;
	}

	public String getInsertedOn(){
		return insertedOn;
	}

	public void setDeletedOn(Object deletedOn){
		this.deletedOn = deletedOn;
	}

	public Object getDeletedOn(){
		return deletedOn;
	}

	public void setRoleId(int roleId){
		this.roleId = roleId;
	}

	public int getRoleId(){
		return roleId;
	}

	public void setDeleteDate(String deleteDate){
		this.deleteDate = deleteDate;
	}

	public String getDeleteDate(){
		return deleteDate;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setContact(String contact){
		this.contact = contact;
	}

	public String getContact(){
		return contact;
	}

	public void setUpdateDate(String updateDate){
		this.updateDate = updateDate;
	}

	public String getUpdateDate(){
		return updateDate;
	}

	public void setOrgId(int orgId){
		this.orgId = orgId;
	}

	public int getOrgId(){
		return orgId;
	}

	public void setAccessToken(Object accessToken){
		this.accessToken = accessToken;
	}

	public Object getAccessToken(){
		return accessToken;
	}

	public void setSyncType(int syncType){
		this.syncType = syncType;
	}

	public int getSyncType(){
		return syncType;
	}

	public void setUpdatedOn(Object updatedOn){
		this.updatedOn = updatedOn;
	}

	public Object getUpdatedOn(){
		return updatedOn;
	}

	public void setAuthToken(Object authToken){
		this.authToken = authToken;
	}

	public Object getAuthToken(){
		return authToken;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setUserLanguage(String userLanguage){
		this.userLanguage = userLanguage;
	}

	public String getUserLanguage(){
		return userLanguage;
	}

	@Override
 	public String toString(){
		return 
			"CreateChannnelModel{" +
			"userMobileNo = '" + userMobileNo + '\'' + 
			",themeData = '" + themeData + '\'' + 
			",designation = '" + designation + '\'' + 
			",isDeleted = '" + isDeleted + '\'' + 
			",email = '" + email + '\'' + 
			",address = '" + address + '\'' + 
			",insertDate = '" + insertDate + '\'' + 
			",insertedOn = '" + insertedOn + '\'' + 
			",deletedOn = '" + deletedOn + '\'' + 
			",roleId = '" + roleId + '\'' + 
			",deleteDate = '" + deleteDate + '\'' + 
			",name = '" + name + '\'' + 
			",contact = '" + contact + '\'' + 
			",updateDate = '" + updateDate + '\'' + 
			",org_Id = '" + orgId + '\'' + 
			",accessToken = '" + accessToken + '\'' + 
			",syncType = '" + syncType + '\'' + 
			",updatedOn = '" + updatedOn + '\'' + 
			",authToken = '" + authToken + '\'' + 
			",id = '" + id + '\'' + 
			",userLanguage = '" + userLanguage + '\'' + 
			"}";
		}
}
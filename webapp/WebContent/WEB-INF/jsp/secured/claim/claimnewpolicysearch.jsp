<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="searchUrl" value="/secured/claim/policysearch" />
<spring:url var="selectUrl" value="/secured/claim/new" />

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="claim.policy_search" /></div>
				<form:form commandName="claimSetupForm" method="post" action="${searchUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="companyId">
		  					<spring:message code="claim.company" />
		  				</label>
	  					<span class="col-sm-9">
  							<form:select path="policySearchCriteria.companyId" class="form-control input-sm" id="companyId">
								<form:option label="Select" value="" />
								<form:options items="${claimSetupForm.companies}" itemLabel="name" itemValue="id" />
							</form:select>
	  					</span>
		   			</div>
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="insurer">
		  					<spring:message code="claim.insurer" />
		  				</label>
		  				<span class="col-sm-9">
  							<form:select path="policySearchCriteria.insurerCode" class="form-control input-sm" id="insurer">
								<form:option label="Select" value="" />
								<form:options items="${claimSetupForm.insurers}" itemLabel="dropdownLabel" itemValue="code" />
							</form:select>
		   				</span>
					</div>
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="policyNo">
		  					<spring:message code="claim.policyNo" />
		  				</label>
	  					<span class="col-sm-3">
  							<form:input path="policySearchCriteria.policyNo" class="form-control input-sm" id="policyNo"/>
	  					</span>		  				
		   				<span class="col-sm-1"></span>
		  				<label class="control-label col-sm-2" for="insuranceClass">
		  					<spring:message code="claim.insuranceClass" />
		  				</label>
		  				<span class="col-sm-3">
  							<form:select path="policySearchCriteria.insuranceClassCode" class="form-control input-sm" id="insuranceClass">
								<form:option label="Select" value="" />
								<form:options items="${claimSetupForm.insuranceClasses}" itemLabel="dropdownLabel" itemValue="code" />
							</form:select>
		   				</span>
					</div>
		   		</div>		
				<div class="panel-footer" align="right">
					<input type="submit" class="btn btn-primary" value="<spring:message code="button.search" />" />
					<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>						
				</div>		   				
				</form:form>
			</div>
		</div>
	</div>
	<c:if test="${claimSetupForm.policySearched}">
		<div class="row">
		    <div class="col-sm-1"></div>
		  	<div class="panel-group col-sm-10">
			    <div class="panel panel-primary">
					<div class="panel-body">
						<datatables:table id="myTableId" data="${claimSetupForm.policies}" rowIdBase="policyId" cssClass="table table-striped" row="row" ext="responsive" autoWidth="false" filterable="false">						
							<datatables:column titleKey="policymgmt.setup.policyNo" cssStyle="padding:10px 10px" sortInitOrder="1" sortInitDirection="asc">
								<a href="${selectUrl}/${url_param_prefix}/${row.policyId}"/><c:out value="${row.policyNo}" /></a>
							</datatables:column>
							<datatables:column property="insuranceClassCode" titleKey="policymgmt.setup.insuranceClass" cssStyle="padding:10px 10px" sortInitOrder="2"/>
							<datatables:column property="companyName" titleKey="policymgmt.setup.company" cssStyle="padding:10px 10px; width:12%" sortInitOrder="3"/>
							<datatables:column property="startDate" titleKey="policymgmt.setup.startDate" cssStyle="padding:10px 10px" sortInitOrder="4" searchable="false" format="{0,date,dd-MM-yyyy}"/>
							<datatables:column property="endDate" titleKey="policymgmt.setup.endDate" cssStyle="padding:10px 10px" sortInitOrder="5" searchable="false" format="{0,date,dd-MM-yyyy}"/>
							<datatables:column property="insurerCode" titleKey="policymgmt.setup.insurerCode" cssStyle="padding:10px 10px" sortInitOrder="6"/>
							<datatables:column property="insurerName" titleKey="policymgmt.setup.insurer" cssStyle="padding:10px 10px; width:12%" sortInitOrder="7"/>
							<datatables:column property="sumInsured" titleKey="policymgmt.setup.sumInsured" cssStyle="padding:10px 10px; width:12%" sortInitOrder="8" format="{0,number,###,###,###,##0.00}"/>
							<datatables:column property="premiumGross" titleKey="policymgmt.setup.totalGrossPremium" cssStyle="padding:10px 10px; width:12%" sortInitOrder="9" format="{0,number,###,###,###,##0.00}"/>
						</datatables:table>
					</div>
				</div>
			</div>
		</div>
	</c:if>
</div>
<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="searchUrl" value="/secured/policymgmt/setup" />
<spring:url var="editUrl" value="/secured/policymgmt/setup/edit" />

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function() {
		$("#effectiveYear").number(true, 0, '', '');
	});
</script>

<div class="container-fluid">
	<div class="row">
		<div class="col-sm-1"></div>
		<div class="panel-group col-sm-12">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="policymgmt.setup.policy_listing" />
				</div>
				<form:form commandName="policySearchForm" method="post"
					action="${searchUrl}" role="form" class="form-horizontal">
					<form:errors element="div" cssClass="errorblock" path="*" />
					<div class="panel-body">
						<div class="form-group row">
							<label class="control-label col-sm-2" for="companyId"> 
								<spring:message code="policymgmt.setup.company" />
							</label>
							<span class="col-sm-9"> 
								<form:select path="criteria.companyId" class="form-control input-sm" id="companyId">
									<form:option label="Select" value="" />
									<form:options items="${policySearchForm.companies}" itemLabel="name" itemValue="id" />
								</form:select>
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-2" for="insurer">
								<spring:message code="policymgmt.setup.insurer" />
							</label>
							<span class="col-sm-9">
								<form:select path="criteria.insurerCode" class="form-control input-sm" id="insurer">
									<form:option label="Select" value="" />
									<form:options items="${policySearchForm.insurers}" itemLabel="dropdownLabel" itemValue="code" />
								</form:select>
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-2" for="policyNo">
								<spring:message code="policymgmt.setup.policyNo" />
							</label>
							<span class="col-sm-3"> 
								<form:input path="criteria.policyNo" class="form-control input-sm" id="policyNo" />
							</span>
							<label class="control-label col-sm-3" for="insuranceClass">
								<spring:message code="policymgmt.setup.insuranceClass" />
							</label>
							<span class="col-sm-3">
								<form:select path="criteria.insuranceClassCode" class="form-control input-sm" id="insuranceClass">
									<form:option label="Select" value="" />
									<form:options items="${policySearchForm.insuranceClasses}" itemLabel="dropdownLabel" itemValue="code" />
								</form:select>
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-2" for="effectiveYear">
								<spring:message code="policymgmt.setup.effectiveYear" />
							</label>
							<span class="col-sm-2">
								<form:input path="criteria.effectiveYear" class="form-control input-sm" id="effectiveYear" maxlength="4" />
								<label class="control-label" for="effectiveYear">
									<spring:message code="policymgmt.setup.effectiveYearSample" />
								</label>							
							</span>
						</div>
					</div>
					<div class="panel-footer" align="right">
						<form:button name="action" value="search" class="btn btn-primary">
							<spring:message code="button.search" />
						</form:button>
						<c:if test="${!policySearchForm.sibUser}">						
							<form:button name="action" value="new" class="btn btn-primary">
								<spring:message code="button.new" />
							</form:button>
						</c:if>
						<form:button name="action" value="back" class="btn btn-primary">
							<spring:message code="button.back" />
						</form:button>
					</div>
				</form:form>
			</div>
		</div>
	</div>
	<c:if test="${policySearchForm.searched}">
		<div class="row">
			<div class="col-sm-1"></div>
			<div class="panel-group col-sm-12">
				<div class="panel panel-primary">
					<div class="panel-body">
						<datatables:table id="policiesTable" data="${policySearchForm.policies}" rowIdBase="policyId" stateSave="true" cssClass="table table-striped" row="row" ext="responsive" autoWidth="false" filterable="false">
							<datatables:column titleKey="policymgmt.setup.policyNo" cssStyle="padding:10px 10px" sortInitOrder="1" sortInitDirection="asc">
								<a href="${editUrl}/${url_param_prefix}/${row.policyId}" data-toggle="tooltip" data-placement="top" data-html="true" title="${row.title}" style="color: blueviolet; font-weight: bold;" />
									<c:out value="${row.policyNo}" />
								</a>
							</datatables:column>
							<datatables:column property="insuranceClassCode" titleKey="policymgmt.setup.insuranceClass" cssStyle="padding:10px 10px" sortInitOrder="2" />
							<datatables:column property="companyName" titleKey="policymgmt.setup.company" cssStyle="padding:10px 10px; width:12%" sortInitOrder="3" />
							<datatables:column property="startDate" titleKey="policymgmt.setup.startDate" cssStyle="padding:10px 10px" sortInitOrder="4" searchable="false" format="{0,date,dd-MM-yyyy}" />
							<datatables:column property="endDate" titleKey="policymgmt.setup.endDate" cssStyle="padding:10px 10px" sortInitOrder="5" searchable="false" format="{0,date,dd-MM-yyyy}" />
							<datatables:column property="insurerCode" titleKey="policymgmt.setup.insurerCode" cssStyle="padding:10px 10px" sortInitOrder="6" />
							<datatables:column property="insurerName" titleKey="policymgmt.setup.insurer" cssStyle="padding:10px 10px; width:12%" sortInitOrder="7" />
							<datatables:column property="sumInsured" titleKey="policymgmt.setup.sumInsured" cssStyle="padding:10px 10px; width:12%" sortInitOrder="8" format="{0,number,###,###,###,##0.00}" />
							<datatables:column property="premiumGross" titleKey="policymgmt.setup.totalGrossPremium" cssStyle="padding:10px 10px; width:12%" sortInitOrder="9" format="{0,number,###,###,###,##0.00}" />
						</datatables:table>
					</div>
				</div>
			</div>
		</div>
	</c:if>
</div>
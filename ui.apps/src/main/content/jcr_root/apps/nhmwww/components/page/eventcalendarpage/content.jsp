<%@page session="false"
          import="com.day.cq.wcm.api.Page"%>
<%@include file="/apps/nhmwww/components/global.jsp"%>
<cq:includeClientLib categories="nhmwww.eventcalendarpage"/>
<cq:includeClientLib categories="cq.widgets"/>
<div class="main-section">
    <div class="small-12 large-text-left columns">
        <cq:include path="title" resourceType="nhmwww/components/functional/title"/>
	</div>
	<cq:include path="par" resourceType="foundation/components/parsys" />
    <div id="searchResult" class="row"></div>
    <div id="showMore" class="row more--results">
        <h5 class="more-results-text more-results-text__directory-search-results">Show more</h5>
    </div>
</div>
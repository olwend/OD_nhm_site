var NHMSearchQuery = new function () {
    var CONST = {
        CONTAINER_DIV_CLASS:    "small-12 columns",
        DISPLAY_SHOW_MORE:      "row event--calendar--more--results",
        EVENT_TITLE_CLASS:      "pl-12",
        EVENT_TITLE_TODAY:      "Today's Events",
        EVENT_MONTH_DATES:      ["Jan",  "Feb", "Mar",
                                 "Apr",  "May", "Jun",
                                 "Jul",  "Aug", "Sep",
                                 "Oct",  "Nov", "Dec"],
        SHORT_MONTH_DATES:      ["Jan",  "Feb", "Mar",
                                 "Apr",  "May", "June",
                                 "July", "Aug", "Sept",
                                 "Oct",  "Nov", "Dec"],
        LONG_MONTH_DATES:       ["January", "February", "March",
                                "April",    "May",      "June",
                                "July",     "August",   "September",
                                "October",  "November", "December"],
        NO_RESULTS_CLASS:       "more-results-text__event--calendar--search--results",
        EVENTS_UL_CLASS:        "large-block-grid-3 medium-block-grid-3 small-block-grid-1",
        PARAGRAPH_CLASS:        "event--calendar--search--result--content--text",
        EVENTS_DATA_URL:        "/content/nhmwww/eventscontent" + "/events",
        HIDE_DIV_CLASS:         "event--calendar--more--results--hide",
        TEXT_DIV_CLASS:         "event--calendar--search--result--item--content",
        IMAGE_CLASS:            "adaptiveimage parbase foundation5image image",
        LI_CLASS:               "event--calendar--search--result",
        H3_CLASS:               "event--calendar--search--result--title"
    };

    var inputs = {};
    
    //Helper function to create the title
    var createTitleH2 = function () {
        var titleH2 = document.createElement("h2");
        titleH2.className = CONST.EVENT_TITLE_CLASS;
        return titleH2;
    };
    
    //Helper function to create the ul
    var createUL = function () {
        var ul = document.createElement("ul");
        ul.className = CONST.EVENTS_UL_CLASS;
        return ul;
    };
    
    //Helper function to clear the results container
    var clearContainer = function () {
        inputs.results = [];
        inputs.container.innerHTML = "";
        inputs.showMore.className = CONST.HIDE_DIV_CLASS;
        inputs.noResultsToday.className = CONST.HIDE_DIV_CLASS;
        inputs.noResults.className = CONST.HIDE_DIV_CLASS;
    };

    //Function to display the results after a search query
    var createResultDiv = function (dateFrom, dateTo) {
        var numOfDays;
        //If not dateFrom starts today
        if (!dateFrom) {
            dateFrom = new Date();
        } else if (dateFrom) {
            dateFrom = getFormattedDate(dateFrom);
        }

        //If not dateTo finish in one year
        if (!dateTo) {
            dateTo = new Date(dateFrom);
            dateTo.setMonth(dateTo.getMonth() + 12);
        } else if (dateTo) {
            dateTo = getFormattedDate(dateTo);
        }

        dateFrom = dateFrom.setHours(0, 0, 0, 0, 0);
        dateTo = dateTo.setHours(0, 0, 0, 0, 0);

        //Gets the interval of days
        numOfDays = Math.floor((dateTo - dateFrom) / 86400000);

        var date = new Date(dateFrom);
        //For each day gets the events
        for (var i = 0; i < numOfDays + 1; i++) {
            var titleH2     = createTitleH2(),
                ul          = createUL(),
                auxResults  = [],
                auxDate     = new Date();

            //Sets the date in the title
            titleH2.innerHTML = parseToEventDate(new Date(auxDate.setDate(date.getDate() + i)), true);  
                                                    
            auxDate = new Date(auxDate).setHours(0, 0, 0, 0, 0);

            for (var j = 0; j < inputs.results.length; j++) {
                for (var k = 0; k < inputs.results[j].dates.length; k++) {
                    var eventDate =  getEventsFormattedDate(inputs.results[j].dates[k].substring(0, inputs.results[j].dates[k].length - 1)).setHours(0, 0, 0, 0, 0);
                    if (eventDate == auxDate) {
                        //Needed to prevent empty dates displayed
                        if (auxResults.length == 0) {
                            inputs.container.appendChild(titleH2);
                            inputs.container.appendChild(ul);
                        }
                        auxResults.push(inputs.results[j]);
                        break;
                    }
                }
            }
            renderLayout(auxResults, ul, true);
        }
    };

    //Helper function to create the results and add the seach button
    var renderLayout = function (results, ul, isFromSearch) {
        for (var i = 0; i < results.length; i++) {
            createSearchResult(results[i], ul, i, isFromSearch);
            if (i == 6 && !isFromSearch) {
                inputs.showMore.className = CONST.DISPLAY_SHOW_MORE;
            }
        }

        //Adds the listener to the show more div
        if (results.length >= 6) {
            inputs.showMore.addEventListener('click', function (e) {
                showMoreEvents(e, ul, inputs.showMore);
            }, false);
        }  
    };

    //Helper function to get a formatted date from the datepicker inputs
    var getFormattedDate = function (date) {
        var stringDate  = date.split(" "),
            dateNumbers = stringDate[1].split("/");

        return new Date(dateNumbers[2], dateNumbers[1] - 1, dateNumbers[0]);
    };
    
    //Helper function to get a formatted date from the eventsJson
    var getEventsFormattedDate = function (date) {
        var stringDate  = date.split(" ");
        return new Date(stringDate[5], CONST.EVENT_MONTH_DATES.indexOf(stringDate[1]), stringDate[2]);
    };

    //Function to compare the dates
    var searchByDate = function (date, dates, isFromDate) {
        for (var i = 0; i < dates.length; i++) {
            var eventDate = getEventsFormattedDate(dates[i].substring(0, dates[i].length - 1)).setHours(0, 0, 0, 0, 0);
            if ((isFromDate && eventDate >= date) || (!isFromDate && eventDate <= date)) {
                return true;
            }
        }
        return false;
    };

    //Helper function to check the keyword against title, description
    var searchByKeyword = function (pattern, element) {
        return element.title.match(pattern) || element.description.match(pattern) || element.keywords.match(pattern); 
    };
    
    //Helper function to check the tag against title, description, keywords or filters
    var searchByTag = function (pattern, element) {
        var isTarget = false;
        
        for (var i = 0; i < element.length; i++) {
            if (element[i] == pattern) {
                isTarget = true;
                break;
            }
        }
        
        return isTarget;
    };

    //Helper function to check the Event against the page
    var isValidEvent = function (eventType) {
        if (eventType == "Science" && sessionStorage.pagePath == "our-science") {
            return true;
        }
        if (eventType == "School" && sessionStorage.pagePath == "schools") {
            return true;
        }
        if (eventType == "Visitor" && sessionStorage.pagePath == "visit") {
            return true;
        }
        if (eventType == "Tring" && sessionStorage.pagePath == "tring-homepage") {
            return true;
        }
        return false;
    };

    //Function to order the search results
    var orderResults = function () {
        var tempResults     = [],
            notAllDay       = [];

        //Order the events by name
        inputs.results = inputs.results.sort(function (a, b) {
            return a.title.localeCompare(b.title);
        });
        //Gets the All day first
        for (var i = 0; i < inputs.results.length; i++) {
            if (getEventTimes(inputs.results[i], false) == "All day") {
                tempResults.push(inputs.results[i]);
            } else {
                notAllDay.push(inputs.results[i]);
            }
        }
        //Order the rest by time
        notAllDay = notAllDay.sort(function (a, b) {
            var timeOfEventA = getEventTimes(a, true),
                timeOfEventB = getEventTimes(b, true);

            if (timeOfEventA > timeOfEventB) {
                return 1;
            }
            if (timeOfEventA < timeOfEventB) {
                return -1;
            }
            return 0;
        });

        tempResults = tempResults.concat(notAllDay);

        return tempResults;
    };

    //Populates the single event li and events ul
    var createSearchResult = function (event, ul, resultsDisplayed, isFromSearch) {
        var li              = document.createElement("li"),
            containerDiv    = document.createElement("div"),
            navigateDiv     = document.createElement("div"),
            a               = document.createElement("a"),
            aH3             = document.createElement("a"),
            imageDiv        = document.createElement("div"),
            img             = document.createElement("img"),
            h3              = document.createElement("h3"),
            textDiv         = document.createElement("div"),
            paragraph       = document.createElement("p");

        containerDiv.className = CONST.CONTAINER_DIV_CLASS;
        navigateDiv.className = CONST.CONTAINER_DIV_CLASS;

        //Sets the Event Link
        if (event.tileLink.localeCompare("") === 0) {
            a.href = event.eventPagePath + ".html";
        } else {
            a.href = event.tileLink;
        }

        li.className = CONST.LI_CLASS;
        imageDiv.className = CONST.IMAGE_CLASS;
        h3.className = CONST.H3_CLASS;
        textDiv.className = CONST.TEXT_DIV_CLASS;
        paragraph.className = CONST.PARAGRAPH_CLASS;


        img.src = event.imageLink;
        aH3.innerHTML = event.title;
        h3.appendChild(aH3);
        paragraph.innerHTML = getEventDates(event.dates) + "<br/>" +
            "Event type: <b>" + getEvents(event.tags, event.eventType) + "</b><br/>" +
            "Time: <b>" + getEventTimes(event, false) + "</b><br/>" +
            "Ticket price: <b>" + event.eventListingPrice + "</b><br/>" +
            event.description;

        imageDiv.appendChild(img);
        a.appendChild(imageDiv);
        a.appendChild(h3);
        containerDiv.appendChild(a);
        textDiv.appendChild(paragraph);
        containerDiv.appendChild(textDiv);
        li.appendChild(containerDiv);

        //Hides the results above 6
        if (resultsDisplayed > 5 && !isFromSearch) {
            li.style.display = "none";
        }

        ul.appendChild(li);
    };

    //Helper function to return the events
    var getEvents = function (tags, eventType) {
        var events = "";

        for (var i = 0; i < tags.length; i++) {
            var tokens      = tags[i].split("/"),
                firstToken  = tokens[0],
                headers     = firstToken.split(":"),
                lastToken   = "";

            if (headers[1].localeCompare("events") === 0) {
                lastToken = tokens[tokens.length - 1];
                lastToken = lastToken.replace("-", " ");

                if (events.localeCompare("") === 0) {
                    events = lastToken;
                } else {
                    events = events.concat(", " + lastToken);
                }
            }
        }

        if (events.localeCompare("") === 0) {
            return eventType;
        } else {
            return events;
        }
    };

    //Helper function to parse the event date
    var getEventDates = function (dates) {
        //If there is only one date
        if (dates.length == 1) {
            return parseToEventDate(getEventsFormattedDate(dates[0].substring(0, dates[0].length - 1)), false);
        }

        //If there are more than one day it gets the last date
        var aux = [];
        for (var i = 0; i < dates.length; i++) {
            var date = getEventsFormattedDate(dates[i].substring(0, dates[i].length - 1));
            aux.push(date.setHours(0, 0, 0, 0, 0));
        }
        var lastDate = aux.reduce(function (a, b) {
            return a > b ? a : b;
        });

        return parseToEventDate(new Date(), false) + " - " + parseToEventDate(new Date(lastDate), false);
    };

    //Helper function to get the times
    var getEventTimes = function (event, shouldGetTimes) {
        var dates       = event.dates,
            allDay      = event.allDay,
            times       = event.times,
            today       = new Date(),
            eventTimes  = [],
            key         = "";

        //Gets the key for today's event
        for (var i = 0; i < dates.length; i++) {
            var date = getEventsFormattedDate(dates[i].substring(0, dates[i].length - 1));
            if (today.setHours(0, 0, 0, 0, 0) == date.setHours(0, 0, 0, 0, 0)) {
                key = dates[i].substr(dates[i].length - 1);
                break;
            }
        }
        if (key != "" && !shouldGetTimes) {
            if (allDay[key] == "true") {
                return "All day";
            }

            eventTimes = times[key].split(",");

            if (eventTimes.length == 1) {
                return eventTimes[0];
            }

            if (eventTimes.length > 1) {
                return "Times vary";
            }
        }

        eventTimes = times[key].split(",");

        return parseInt(eventTimes[0].replace(':', ''));
    };

    //Helper function to convert the dates to string and to parse the date to the correct format
    var parseToEventDate = function (date, isLongMonth) {
        var day         = date.getDate(),
            monthIndex  = date.getMonth(),
            year        = date.getFullYear(),
            monthName   = isLongMonth ? CONST.LONG_MONTH_DATES[monthIndex] : CONST.SHORT_MONTH_DATES[monthIndex];

        return day.toString() + " " + monthName + " " + year.toString();
    };

    //Function to display more events
    var showMoreEvents = function (e, ul, showMore) {
        e.preventDefault();
        var listItem = ul.getElementsByTagName("li"),
            counter = 0;

        for (var i = 0; i < listItem.length; i++) {
            if (counter == 6) {
                return;
            }
            if (listItem[i].offsetParent === null) {
                listItem[i].style.display = "block";
                counter++;
            }
        }
        showMore.className = CONST.HIDE_DIV_CLASS;
        //Removes the vent listener
        this.removeEventListener('click', arguments.callee, false);
    };
    
    //Helper function to check the today's events
    var checkTodayEvents = function (event) {
        var eventType = event.eventType;
        
        if (isValidEvent(eventType)) {
           return true;
        } else {
            //If not checks the tags
            var tags = event.tags;
            for (var k = 0; k < tags.length; k++) {
                var tag = tags[k].split("/");
                if (isValidEvent(tag[tag.length - 1])) {
                    return true;
                }
            }
        }
        return false;
    }

    this.init = function () {
        //Stores the events JSON in sessionStorage and displays Today Events
        var eventsData  = CQ.HTTP.get(CQ.HTTP.externalize(CONST.EVENTS_DATA_URL)),
            pagePath    = CQ.WCM.getPagePath();

        sessionStorage.events = eventsData.responseText;

        pagePath = pagePath.split("/");
        sessionStorage.pagePath = pagePath[pagePath.length - 2];

        inputs = {
            eventsJson:     JSON.parse(sessionStorage.events).Events,
            carousel:       document.getElementsByClassName("nhm-carousel"),
            showMore:       document.getElementById("showMore"),
            noResultsToday: document.getElementById("noResultsToday"),
            noResults:      document.getElementById("noResults"),
            container:      document.getElementById("searchResult"),
            results:        []
        };
    };
    
    //Populates the single events result content
    this.displayTodayEvents = function () {
        clearContainer();
        
        //Prevents if no carousel
        var carousel = inputs.carousel[0];
        if (carousel != undefined || carousel != null) {
            carousel.style.display = "block";
        }

        var titleH2 = createTitleH2(),
            ul      = createUL(),
            today   = new Date();

        titleH2.innerHTML = CONST.EVENT_TITLE_TODAY;
        
        for (var i = 0; i < inputs.eventsJson.length; i++) {
            var eventsJson  = inputs.eventsJson[i];
            for (var j = 0; j < eventsJson.dates.length; j++) {
                var date = getEventsFormattedDate(eventsJson.dates[j].substring(0, eventsJson.dates[j].length - 1));
                //If date == today
                if (today.setHours(0, 0, 0, 0, 0) == date.setHours(0, 0, 0, 0, 0)) {
                    //Checks the Event Type
                    if (checkTodayEvents(eventsJson)) {
                        inputs.results.push(eventsJson);
                    }
                    break;
                }
            }
        }

        //Displays the results
        if (inputs.results.length > 0) {
            //Appends the title
            inputs.container.appendChild(titleH2);
            inputs.container.appendChild(ul);

            inputs.results = orderResults();
            renderLayout(inputs.results, ul, false);
        }

        //Displays the no results for today message
        else {
            inputs.noResultsToday.className = CONST.NO_RESULTS_CLASS;
        }
    };

    //Function which search according to the criteria
    this.displaySearchEvents = function (keywordsInput, filterOne, filterTwo, dateFrom, dateTo) {
        clearContainer();

        //Prevents if no carousel
        var carousel = inputs.carousel[0];
        if (carousel != undefined || carousel != null) {
            carousel.style.display = "none";
        }

        for (var i = 0; i < inputs.eventsJson.length; i++) {
            var isOurEvent  = false,
                pattern     = "",
                eventsJson  = inputs.eventsJson[i],
                tags        = eventsJson.tags;

            isOurEvent = checkTodayEvents(eventsJson);
            
            //If we have keyword and the event matched with our type
            if (keywordsInput && isOurEvent) {
                pattern = new RegExp(keywordsInput, 'i');

                //If the keyword matched with title, description or keywords
                if (!searchByKeyword(pattern, eventsJson)) {
                    isOurEvent = false;
                }
            }
            //If the filter matches with the tags
            if (filterOne != "none" && isOurEvent) {
                if (!searchByTag(filterOne, tags)) {
                    isOurEvent = false;
                }
            }
            if (filterTwo != "none" && isOurEvent) {
                if (!searchByTag(filterTwo, tags)) {
                    isOurEvent = false;
                }
            }
            //If the date matches
            if (dateFrom && isOurEvent) {
                var date = getFormattedDate(dateFrom);
                if (!searchByDate(date, eventsJson.dates, true)) {
                    isOurEvent = false;
                }
            }
            if (dateTo && isOurEvent) {
                var date = getFormattedDate(dateTo);
                if (!searchByDate(date, eventsJson.dates, false)) {
                    isOurEvent = false;
                }
            }
            //Add the event if matches the query
            if (isOurEvent) {
                inputs.results.push(eventsJson);
            }
        }

        //Displays the results
        if (inputs.results.length > 0) {
            inputs.results = orderResults();
            createResultDiv(dateFrom, dateTo);
        }
        //Displays the error msg
        else {
            inputs.noResults.className = CONST.NO_RESULTS_CLASS;
        }
    };
};

$(document).ready(function () {
    NHMSearchQuery.init();
    NHMSearchQuery.displayTodayEvents();
});
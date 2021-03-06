/* Ensure the BS object exists in the window scope. */
window.hasOwnProperty = function(obj){
	return (this[obj]) ? true : false;
};

if (!window.hasOwnProperty("NHM")) {
    window.NHM = {};
}

/**
 * @class NHM.LinkListPathFieldWidget
 * @extends CQ.form.CompositeField
 * This is a custom path field with link text and target based on {@link CQ.form.CompositeField}.
 * @constructor
 * Creates a new CustomWidget.
 * @param {Object} config The config object
 */

NHM.LinkListPathFieldWidget = CQ.Ext.extend(CQ.form.CompositeField, {

    hiddenField: null,
    linkText: null,
    linkURL: null,
    openInNewWindow: null,
    
	constructor: function(config) {
		config = config || { };
        var defaults = {
            "border": true,
            "layout": "form",
            "labelSeparator": ":",
            "padding": "10px"
        };
        config = CQ.Util.applyDefaults(config, defaults);
        NHM.LinkListPathFieldWidget.superclass.constructor.call(this, config);
    },

    //overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        NHM.LinkListPathFieldWidget.superclass.initComponent.call(this);

        // Link text
        this.add(new CQ.Ext.form.Label({
            text: "Link Title"
        }));

        this.linkText = new CQ.Ext.form.TextField({
           width:"100%",
           listeners: {
        	   	change: {
    	   			scope:this,
                   	fn:this.updateHidden,
	            },
	       		dialogclose: {
	       			scope: this,
	       			fn: this.updateHidden,
	       		},
	       		valid: {
	       			scope: this,
	       			fn: this.updateHidden,
	       		}
            }
        });
        this.add(this.linkText);

        // Link URL
        this.add(new CQ.Ext.form.Label({
            text: "URL"
        }));
        this.linkURL = new CQ.form.PathField({
        	width:"100%",
            listeners: {
        	   	change: {
    	   			scope:this,
                   	fn:this.updateHidden,
	            },
	       		dialogclose: {
	       			scope: this,
	       			fn: this.updateHidden,
	       		},
	       		valid: {
	       			scope: this,
	       			fn: this.updateHidden,
	       		}
            }
        });
        this.add(this.linkURL);
        
        // Link openInNewWindow
        this.add(new CQ.Ext.form.Label({
            text: "New Window"
        }));
        this.openInNewWindow = new CQ.Ext.form.Checkbox({
           text: "New Window",
           fieldLabel:"Link Text: ",
           listeners: {
	       	   	change: {
		   			scope:this,
	               	fn:this.updateHidden,
	            },
	       		dialogclose: {
	       			scope: this,
	       			fn: this.updateHidden,
	       		},
	       		valid: {
	       			scope: this,
	       			fn: this.updateHidden,
	       		}
            }
        });
        this.add(this.openInNewWindow);
        
        // Hidden field
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

    },


    setValue: function(value) {
        var link = JSON.parse(value);
        this.linkText.setValue(link.text);
        this.linkURL.setValue(link.url);
        this.openInNewWindow.setValue(link.openInNewWindow);
        this.hiddenField.setValue(value);
    },

//	setFieldsValue: function(value) {
//        var link = JSON.parse(value);
//        this.linkText.setValue(link.text);
//        this.linkURL.setValue(link.url);
//        this.openInNewWindow.setValue(link.openInNewWindow);
//        this.hiddenField.setValue(value);       
//    },

    getValue: function() {
        return this.getRawValue();
    },

    getRawValue: function() {
        var link = {"url": this.linkURL.getValue(),
            "text": this.linkText.getValue(),
            "openInNewWindow": this.openInNewWindow.getValue()
        };
        return JSON.stringify(link);
    },

    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    }
});

CQ.Ext.reg('linklistpathfield', NHM.LinkListPathFieldWidget);
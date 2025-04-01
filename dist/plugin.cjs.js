'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var core = require('@capacitor/core');

const ZendeskSupport = core.registerPlugin('ZendeskSupport', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.ZendeskSupportWeb()),
});

class ZendeskSupportWeb extends core.WebPlugin {
    async initialize(options) {
        console.log('initialize not implemented on web yet!', options);
    }
    async setAnonymousIdentity(options) {
        console.log('setAnonymousIdentity not implemented on web yet!', options);
    }
    async setIdentity(option) {
        console.log('setIdentity not implemented on web yet!', option);
    }
    async showHelpCenter(options) {
        console.log('showHelpCenter not implemented on web yet!', options);
    }
    async showTicketRequest(options) {
        console.log('showTicketRequest not implemented on web yet!', options);
    }
    async showUserTickets() {
        console.log('showUserTickets not implemented on web yet!');
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    ZendeskSupportWeb: ZendeskSupportWeb
});

exports.ZendeskSupport = ZendeskSupport;
//# sourceMappingURL=plugin.cjs.js.map

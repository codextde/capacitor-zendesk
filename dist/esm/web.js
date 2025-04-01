import { WebPlugin } from '@capacitor/core';
export class ZendeskSupportWeb extends WebPlugin {
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
//# sourceMappingURL=web.js.map
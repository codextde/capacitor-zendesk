import { WebPlugin } from '@capacitor/core';

import type { ZendeskPlugin, AnonymousOptions, HelpCenterOptions, IdentityOption, InitializeOptions, TicketRequestOptions } from './definitions';


export class ZendeskWeb extends WebPlugin implements ZendeskPlugin {
  
  async initialize(options: InitializeOptions): Promise<void> {
    console.log('initialize not implemented on web yet!', options);
  }

  async setAnonymousIdentity(options: AnonymousOptions): Promise<void> {
    console.log('setAnonymousIdentity not implemented on web yet!', options);
  }

  async setIdentity(option: IdentityOption): Promise<void> {
    console.log('setIdentity not implemented on web yet!', option);
  }

  async showHelpCenter(options: HelpCenterOptions): Promise<void> {
    console.log('showHelpCenter not implemented on web yet!', options);
  }

  async showTicketRequest(options: TicketRequestOptions): Promise<void> {
    console.log('showTicketRequest not implemented on web yet!', options);
  }

  async showUserTickets(): Promise<void> {
    console.log('showUserTickets not implemented on web yet!');
  }
}


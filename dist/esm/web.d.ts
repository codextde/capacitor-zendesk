import { WebPlugin } from '@capacitor/core';
import type { ZendeskPlugin, AnonymousOptions, HelpCenterOptions, IdentityOption, InitializeOptions, TicketRequestOptions } from './definitions';
export declare class ZendeskWeb extends WebPlugin implements ZendeskPlugin {
    initialize(options: InitializeOptions): Promise<void>;
    setAnonymousIdentity(options: AnonymousOptions): Promise<void>;
    setIdentity(option: IdentityOption): Promise<void>;
    showHelpCenter(options: HelpCenterOptions): Promise<void>;
    showTicketRequest(options: TicketRequestOptions): Promise<void>;
    showUserTickets(): Promise<void>;
}

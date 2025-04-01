import { WebPlugin } from '@capacitor/core';
import type { AnonymousOptions, HelpCenterOptions, IdentityOption, InitializeOptions, TicketRequestOptions, ZendeskSupportPlugin } from './definitions';
export declare class ZendeskSupportWeb extends WebPlugin implements ZendeskSupportPlugin {
    initialize(options: InitializeOptions): Promise<void>;
    setAnonymousIdentity(options: AnonymousOptions): Promise<void>;
    setIdentity(option: IdentityOption): Promise<void>;
    showHelpCenter(options: HelpCenterOptions): Promise<void>;
    showTicketRequest(options: TicketRequestOptions): Promise<void>;
    showUserTickets(): Promise<void>;
}

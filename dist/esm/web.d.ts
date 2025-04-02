import { WebPlugin } from '@capacitor/core';
import type { ZendeskPlugin } from './definitions';
export declare class ZendeskWeb extends WebPlugin implements ZendeskPlugin {
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}

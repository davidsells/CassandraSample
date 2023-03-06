import { IParcel } from 'app/shared/model/parcel.model';
import { ContainerSize } from 'app/shared/model/enumerations/container-size.model';

export interface IContainer {
  id?: string;
  containerNumber?: string | null;
  size?: ContainerSize | null;
  parcels?: IParcel[] | null;
}

export const defaultValue: Readonly<IContainer> = {};

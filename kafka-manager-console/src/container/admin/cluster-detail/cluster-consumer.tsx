import * as React from 'react';

import { Table, Modal, Tooltip } from 'component/antd';
import { observer } from 'mobx-react';
import Url from 'lib/url-parser';
import {IGroupLag, INumberMap, IOffset, IXFormWrapper} from 'types/base-type';
import { SearchAndFilterContainer } from 'container/search-filter';
import { pagination } from 'constants/table';
import { admin } from 'store/admin';
import { getConsumerDetails } from 'lib/api';
import './index.less';

@observer
export class ClusterConsumer extends SearchAndFilterContainer {
  public clusterId: number;
  public consumerDetails: INumberMap;

  public state = {
    searchKey: '',
    detailsVisible: false,
  };

  public columns = [{
    title: '消费组名称',
    dataIndex: 'consumerGroup',
    key: 'consumerGroup',
    width: '70%',
    sorter: (a: IGroupLag, b: IGroupLag) => a.consumerGroup.charCodeAt(0) - b.consumerGroup.charCodeAt(0),
    render: (text: string) => <Tooltip placement="bottomLeft" title={text} >{text}</Tooltip>,
  }, {
    title: '消费组Lag',
    dataIndex: 'lag',
    key: 'lag',
    width: '20%',
    render: (t: number) => t,
  }, {
    title: '详情',
    key: 'detail-lag',
    width: '10%',
    render: (t: string, item: IGroupLag) => {
      return (<a onClick={() => this.getConsumeDetails(item)}>消费详情</a>);
    },
  }];
  private xFormModal: IXFormWrapper;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public getConsumeDetails(record: IGroupLag) {
      this.consumerDetails = record.topicLags;
      this.setState({ detailsVisible: true });

  }

  public handleDetailsOk() {
    this.setState({ detailsVisible: false });
  }

  public handleDetailsCancel() {
    this.setState({ detailsVisible: false });
  }

  public getData<T extends IGroupLag>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IGroupLag) =>
      (item.consumerGroup !== undefined && item.consumerGroup !== null) && item.consumerGroup.toLowerCase().includes(searchKey as string),
    ) : origin;
    return data;
  }

  public componentDidMount() {
    admin.getClusterConsumer(this.clusterId);
  }

  public render() {
    let details: any[];
    details = this.consumerDetails ?  Object.keys(this.consumerDetails).map((ele) => {
      return {
        key: this.consumerDetails[ele],
        topicName: ele
      };
    }) : [];

    const consumptionColumns = [{
      title: '消费的Topic列表',
      dataIndex: 'topicName'
    }, {
      title: 'group-topic Lag',
      dataIndex: 'key'
    }];

    return (
      <>
        <div className="k-row">
          <ul className="k-tab">
            <li>{this.props.tab}</li>
            {this.renderSearch('', '请输入消费组名称')}
          </ul>
          <Table
            columns={this.columns}
            dataSource={this.getData(admin.consumerData)}
            pagination={pagination}
            rowKey="key"
          />
        </div>
        <Modal
          title="消费详情"
          visible={this.state.detailsVisible}
          onOk={() => this.handleDetailsOk()}
          onCancel={() => this.handleDetailsCancel()}
          maskClosable={false}
          footer={null}
        // centered={true}
        >
          <Table
            columns={consumptionColumns}
            dataSource={details}
            // 运维管控－消费组列表－详情
            pagination={details.length < 10 ? false : pagination}
            rowKey="key"
            scroll={{ y: 260 }}
          />
        </Modal>
      </>
    );
  }
}

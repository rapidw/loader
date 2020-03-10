import React, {useState} from "react";
import { Table, Card, Button } from "antd";
import { PageHeaderWrapper } from '@ant-design/pro-layout';

const AgentList: React.FC = () => {
  const columns = [
    {
      title: 'Name',
      dataIndex: 'name',
    },
    {
      title: 'Age',
      dataIndex: 'age',
    },
    {
      title: 'Address',
      dataIndex: 'address',
    },
  ];

  const data = [];
  for (let i = 0; i < 46; i++) {
    data.push({
      key: i,
      name: `Edward King ${i}`,
      age: 32,
      address: `London, Park Lane no. ${i}`,
    });
  }

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const start = () => {
    setLoading(true)
    // ajax request after empty completing
    setTimeout(() => {
      setLoading(false);
      setSelectedRowKeys([])
    }, 1000);
  };

  const onSelectChange = (selectedRowKeys: any)=> {
      console.log('selectedRowKeys changed: ', selectedRowKeys);
      setSelectedRowKeys(selectedRowKeys);
    };

  const rowSelection = {
        selectedRowKeys,
        onChange: onSelectChange,
  };
  const hasSelected = selectedRowKeys.length > 0;

  return (
    <PageHeaderWrapper>
      <Card>
        <div>
          <div style={{ marginBottom: 16 }}>
            <Button onClick={start} disabled={!hasSelected} loading={loading}>
              Reload
            </Button>
            <Button type="primary" style={{marginLeft:"20px"}}>Add Agent</Button>
            <span style={{ marginLeft: 8 }}>
            {hasSelected ? `Selected ${selectedRowKeys.length} items` : ''}
          </span>
          </div>
          <Table rowSelection={rowSelection} columns={columns} dataSource={data} />
        </div>
      </Card>
    </PageHeaderWrapper>
  )
}

export default AgentList;

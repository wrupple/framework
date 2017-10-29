package com.wrupple.batch.service

import com.wrupple.muba.bpm.domain.WorkRequest

trait BatchMessageDelegate {
  def send(job: WorkRequest): WorkRequest

}

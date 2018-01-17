package com.wrupple.batch.service

trait BatchMessageDelegate {
  def send(job: WorkRequest): WorkRequest

}
